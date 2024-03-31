package com.soma.doubanen.controllers;

import com.soma.doubanen.domains.dto.MediaDto;
import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.enums.MediaType;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.MediaService;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medias")
public class MediaController {

  private final MediaService mediaService;

  private final Mapper<MediaEntity, MediaDto> musicMapper;

  public MediaController(MediaService mediaService, Mapper<MediaEntity, MediaDto> musicMapper) {
    this.mediaService = mediaService;
    this.musicMapper = musicMapper;
  }

  @PostMapping()
  public ResponseEntity<MediaDto> createMedia(@RequestBody MediaDto mediaDto) {
    MediaEntity mediaEntity = musicMapper.mapFrom(mediaDto);
    Optional<MediaEntity> result = mediaService.save(mediaEntity, null);
    return result
        .map(
            savedMediaEntity -> {
              MediaDto savedMediaDto = musicMapper.mapTo(savedMediaEntity);
              return new ResponseEntity<>(savedMediaDto, HttpStatus.CREATED);
            })
        .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @GetMapping(path = "{id}")
  public ResponseEntity<MediaDto> getMedia(@PathVariable("id") Long id) {
    Optional<MediaEntity> foundMedia = mediaService.findOne(id);
    return foundMedia
        .map(
            musicEntity -> {
              MediaDto mediaDto = musicMapper.mapTo(musicEntity);
              return new ResponseEntity<>(mediaDto, HttpStatus.OK);
            })
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping()
  public Page<MediaDto> listMedias(Pageable pageable) {
    Page<MediaEntity> musics = mediaService.findAll(pageable);
    return musics.map(musicMapper::mapTo);
  }

  @PutMapping(path = "{id}")
  public ResponseEntity<MediaDto> updateMedia(
      @PathVariable("id") Long id, @RequestBody MediaDto mediaDto) {
    if (mediaService.notExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    MediaEntity entity = musicMapper.mapFrom(mediaDto);
    Optional<MediaEntity> result = mediaService.save(entity, id);
    return result
        .map(
            savedMediaEntity -> {
              MediaDto savedMediaDto = musicMapper.mapTo(savedMediaEntity);
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

    MediaEntity mediaEntity = musicMapper.mapFrom(mediaDto);
    MediaEntity updatedMedia = mediaService.partialUpdate(id, mediaEntity);
    return new ResponseEntity<>(musicMapper.mapTo(updatedMedia), HttpStatus.OK);
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
    return new ResponseEntity<>(musics.map(musicMapper::mapTo), HttpStatus.OK);
  }
}
