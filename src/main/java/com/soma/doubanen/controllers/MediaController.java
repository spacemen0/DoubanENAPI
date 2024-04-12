package com.soma.doubanen.controllers;

import com.soma.doubanen.domains.dto.MediaDto;
import com.soma.doubanen.domains.entities.AuthorEntity;
import com.soma.doubanen.domains.entities.ImageEntity;
import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.enums.AuthorType;
import com.soma.doubanen.domains.enums.ImageType;
import com.soma.doubanen.domains.enums.MediaGenre;
import com.soma.doubanen.domains.enums.MediaType;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.AuthorService;
import com.soma.doubanen.services.ImageService;
import com.soma.doubanen.services.MediaService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/medias")
public class MediaController {

  private final MediaService mediaService;

  private final ImageService imageService;

  private final AuthorService authorService;

  private final Mapper<MediaEntity, MediaDto> mediaMapper;

  public MediaController(
      MediaService mediaService,
      ImageService imageService,
      AuthorService authorService,
      Mapper<MediaEntity, MediaDto> mediaMapper) {
    this.mediaService = mediaService;
    this.imageService = imageService;
    this.authorService = authorService;
    this.mediaMapper = mediaMapper;
  }

  @PostMapping(consumes = {"multipart/form-data"})
  public ResponseEntity<MediaDto> createMedia(
      @ModelAttribute MediaDto mediaDto,
      @RequestParam(value = "image", required = false) MultipartFile image,
      @RequestParam(value = "authorName", required = false) String authorName,
      @RequestParam(value = "authorType", required = false) AuthorType type,
      @RequestParam(value = "authorGenres", required = false) List<MediaGenre> genres,
      @RequestParam(value = "authorId", required = false) Long authorId) {
    //    List<MediaGenre> mediaGenres = genres.stream()
    //            .map(MediaGenre::valueOf)
    //            .toList();
    MediaEntity mediaEntity = mediaMapper.mapFrom(mediaDto);
    System.out.println(mediaDto);
    Optional<MediaEntity> result = mediaService.save(mediaEntity, null);
    if (result.isPresent()) {
      MediaEntity media = result.get();
      if (image != null) {

        byte[] data;
        try {
          data = imageService.compressImage(image.getBytes());
        } catch (IOException e) {
          return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        ImageEntity imageEntity =
            ImageEntity.builder()
                .imageData(data)
                .objectId(media.getId())
                .type(ImageType.MediaArt)
                .build();
        ImageEntity savedImage = imageService.save(imageEntity);
        media.setImageUrl("/images/" + savedImage.getId());
        AuthorEntity author;
        if (authorId == null) {
          author = AuthorEntity.builder().type(type).name(authorName).build();
          if (genres != null) author.setGenres(genres);
        } else {
          author = authorService.findOne(authorId).orElseThrow();
        }
        author.setMediaEntities(List.of(media));
        media.setAuthorEntity(author);

        Optional<MediaEntity> optionalMedia = mediaService.save(media, media.getId());
        return optionalMedia
            .map(
                mediaEntity1 -> {
                  MediaDto mediaDto1 = mediaMapper.mapTo(mediaEntity1);
                  return new ResponseEntity<>(mediaDto1, HttpStatus.CREATED);
                })
            .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
      }
    }
    return (new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @GetMapping(path = "{id}")
  public ResponseEntity<MediaDto> getMedia(@PathVariable("id") Long id) {
    Optional<MediaEntity> foundMedia = mediaService.findOne(id);
    return foundMedia
        .map(
            mediaEntity -> {
              MediaDto mediaDto = mediaMapper.mapTo(mediaEntity);
              return new ResponseEntity<>(mediaDto, HttpStatus.OK);
            })
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping()
  public Page<MediaDto> listMedias(Pageable pageable) {
    Page<MediaEntity> musics = mediaService.findAll(pageable);
    return musics.map(mediaMapper::mapTo);
  }

  @PutMapping(path = "{id}")
  public ResponseEntity<MediaDto> updateMedia(
      @PathVariable("id") Long id, @RequestBody MediaDto mediaDto) {
    if (mediaService.notExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    MediaEntity entity = mediaMapper.mapFrom(mediaDto);
    Optional<MediaEntity> result = mediaService.save(entity, id);
    return result
        .map(
            savedMediaEntity -> {
              MediaDto savedMediaDto = mediaMapper.mapTo(savedMediaEntity);
              return new ResponseEntity<>(savedMediaDto, HttpStatus.OK);
            })
        .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @PatchMapping(path = "{id}")
  public ResponseEntity<MediaDto> partialUpdate(
      @PathVariable("id") Long id, @RequestBody MediaDto mediaDto) {
    if (mediaService.notExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    MediaEntity mediaEntity = mediaMapper.mapFrom(mediaDto);
    MediaEntity updatedMedia = mediaService.partialUpdate(id, mediaEntity);
    return new ResponseEntity<>(mediaMapper.mapTo(updatedMedia), HttpStatus.OK);
  }

  @DeleteMapping(path = "{id}")
  public ResponseEntity<Void> deleteMedia(@PathVariable("id") Long id) {
    mediaService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping(path = "/count")
  public ResponseEntity<Long> countMedias() {
    return new ResponseEntity<>(mediaService.countAll(), HttpStatus.OK);
  }

  @GetMapping(path = "/count/{type}")
  public ResponseEntity<Long> countMediasByType(@PathVariable String type) {
    try {
      MediaType.valueOf(type);
    } catch (IllegalArgumentException error) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(mediaService.countByType(MediaType.valueOf(type)), HttpStatus.OK);
  }

  @GetMapping(params = {"type", "page", "size"})
  public ResponseEntity<Page<MediaDto>> listMediasByType(
      @RequestParam String type, @RequestParam Integer page, @RequestParam Integer size) {
    try {
      MediaType.valueOf(type);
    } catch (IllegalArgumentException error) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    Page<MediaEntity> musics =
        mediaService.findAll(PageRequest.of(page - 1, size), MediaType.valueOf(type));
    return new ResponseEntity<>(musics.map(mediaMapper::mapTo), HttpStatus.OK);
  }
}
