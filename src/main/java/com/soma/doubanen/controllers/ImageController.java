package com.soma.doubanen.controllers;

import com.soma.doubanen.domains.dto.ImageDto;
import com.soma.doubanen.domains.entities.ImageEntity;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.ImageService;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.DataFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/images")
public class ImageController {
  private final ImageService imageService;

  private final Mapper<ImageEntity, ImageDto> imageMapper;

  public ImageController(ImageService imageService, Mapper<ImageEntity, ImageDto> imageMapper) {
    this.imageService = imageService;
    this.imageMapper = imageMapper;
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity<?> getImage(@PathVariable("id") Long id) {
    Optional<ImageEntity> imageEntity = imageService.findById(id);
    if (imageEntity.isPresent()) {
      byte[] data;
      try {
        data = imageService.decompressImage(imageMapper.mapTo(imageEntity.get()).getImageData());
      } catch (DataFormatException | IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error decompressing image data");
      }
      return ResponseEntity.status(HttpStatus.OK)
          .contentType(MediaType.valueOf(MediaType.IMAGE_JPEG_VALUE))
          .body(data);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
    }
  }
}
