package com.soma.doubanen.controllers;

import com.soma.doubanen.domains.dto.MediaDto;
import com.soma.doubanen.domains.dto.MediaStatusDto;
import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.entities.MediaStatusEntity;
import com.soma.doubanen.domains.enums.MediaStatus;
import com.soma.doubanen.domains.enums.MediaType;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.MediaService;
import com.soma.doubanen.services.MediaStatusService;
import com.soma.doubanen.services.TokenService;
import com.soma.doubanen.services.UserService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/media-statuses")
public class MediaStatusController {

  private final MediaStatusService mediaStatusService;
  private final Mapper<MediaStatusEntity, MediaStatusDto> mediaStatusMapper;
  private final MediaService mediaService;

  private final TokenService tokenService;
  private final UserService userService;

  private final Mapper<MediaEntity, MediaDto> mediaMapper;

  public MediaStatusController(
      MediaStatusService mediaStatusService,
      Mapper<MediaStatusEntity, MediaStatusDto> mediaStatusMapper,
      MediaService mediaService,
      TokenService tokenService,
      UserService userService,
      Mapper<MediaEntity, MediaDto> mediaMapper) {
    this.mediaStatusService = mediaStatusService;
    this.mediaStatusMapper = mediaStatusMapper;
    this.mediaService = mediaService;
    this.tokenService = tokenService;
    this.userService = userService;
    this.mediaMapper = mediaMapper;
  }

  @PostMapping()
  public ResponseEntity<MediaStatusDto> createMediaStatus(
      @RequestBody MediaStatusDto mediaStatusDto,
      @RequestHeader(name = "Authorization") String auth) {
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(mediaStatusDto.getUserId())))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    MediaStatusEntity mediaStatus = mediaStatusMapper.mapFrom(mediaStatusDto);
    Optional<MediaStatusEntity> result = mediaStatusService.save(mediaStatus, null);
    return result
        .map(
            mediaStatusEntity ->
                new ResponseEntity<>(
                    mediaStatusMapper.mapTo(mediaStatusEntity), HttpStatus.CREATED))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity<MediaStatusDto> getMediaStatus(@PathVariable("id") Long id) {
    Optional<MediaStatusEntity> foundMediaStatus = mediaStatusService.findOne(id);
    return foundMediaStatus
        .map(
            mediaStatusEntity -> {
              MediaStatusDto mediaStatusDto = mediaStatusMapper.mapTo(mediaStatusEntity);
              return new ResponseEntity<>(mediaStatusDto, HttpStatus.OK);
            })
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping()
  public List<MediaStatusDto> listMediaStatuses() {
    List<MediaStatusEntity> mediaStatuses = mediaStatusService.findAll();
    return mediaStatuses.stream().map(mediaStatusMapper::mapTo).collect(Collectors.toList());
  }

  @PutMapping(path = "/{id}")
  public ResponseEntity<MediaStatusDto> updateMediaStatus(
      @PathVariable("id") Long id,
      @RequestBody MediaStatusDto mediaStatusDto,
      @RequestHeader(name = "Authorization") String auth) {
    if (mediaStatusService.notExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(mediaStatusDto.getUserId())))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    MediaStatusEntity entity = mediaStatusMapper.mapFrom(mediaStatusDto);
    Optional<MediaStatusEntity> result = mediaStatusService.save(entity, id);
    return result
        .map(
            mediaStatusEntity ->
                new ResponseEntity<>(
                    mediaStatusMapper.mapTo(mediaStatusEntity), HttpStatus.CREATED))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
  }

  @PatchMapping(path = "/{id}")
  public ResponseEntity<MediaStatusDto> partialUpdateMediaStatus(
      @PathVariable("id") Long id,
      @RequestBody MediaStatusDto mediaStatusDto,
      @RequestHeader(name = "Authorization") String auth) {
    if (mediaStatusService.notExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(mediaStatusDto.getUserId())))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    MediaStatusEntity mediaStatusEntity = mediaStatusMapper.mapFrom(mediaStatusDto);
    MediaStatusEntity updatedMediaStatus = mediaStatusService.partialUpdate(id, mediaStatusEntity);
    return new ResponseEntity<>(mediaStatusMapper.mapTo(updatedMediaStatus), HttpStatus.OK);
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deleteMediaStatus(
      @PathVariable("id") Long id, @RequestHeader(name = "Authorization") String auth) {
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    Optional<MediaStatusEntity> mediaStatusEntity = mediaStatusService.findOne(id);
    if (mediaStatusEntity.isPresent()) {
      if (!username.equals(userService.getUsernameById(mediaStatusEntity.get().getUserId())))
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    mediaStatusService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping(params = {"userId", "mediaType", "mediaStatus"})
  public List<MediaDto> getUserMediaStatuses(
      @RequestParam Long userId,
      @RequestParam MediaType mediaType,
      @RequestParam MediaStatus mediaStatus) {
    List<MediaStatusEntity> mediaStatuses =
        mediaStatusService.findByTypeAndUserIdAndStatus(mediaType, userId, mediaStatus);
    List<MediaEntity> correspondingMediaEntities =
        mediaStatuses.stream()
            .map(
                correspondingMediaStatus ->
                    mediaService.findOne(correspondingMediaStatus.getMediaId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    return correspondingMediaEntities.stream().map(mediaMapper::mapTo).collect(Collectors.toList());
  }

  @GetMapping(params = {"userId", "mediaId"})
  public ResponseEntity<MediaStatusDto> getUserMediaStatus(
      @RequestParam Long userId, @RequestParam Long mediaId) {
    Optional<MediaStatusEntity> mediaStatus =
        mediaStatusService.findByUserIdAndMediaId(userId, mediaId);
    return mediaStatus
        .map(
            mediaStatusEntity ->
                new ResponseEntity<>(mediaStatusMapper.mapTo(mediaStatusEntity), HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping(params = "userId")
  public List<MediaDto> getUserCurrent(@RequestParam Long userId) {
    List<MediaStatusEntity> mediaStatuses = mediaStatusService.getUserCurrentOn(userId);
    List<MediaEntity> correspondingMediaEntities =
        mediaStatuses.stream()
            .map(
                correspondingMediaStatus ->
                    mediaService.findOne(correspondingMediaStatus.getMediaId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    return correspondingMediaEntities.stream().map(mediaMapper::mapTo).collect(Collectors.toList());
  }

  @GetMapping(params = {"userId", "page", "size", "mediaStatus"})
  public ResponseEntity<Page<MediaDto>> getAllMediaStatuses(
      @RequestParam Long userId,
      @RequestParam Integer page,
      @RequestParam Integer size,
      @RequestParam MediaStatus mediaStatus) {

    return new ResponseEntity<>(
        mediaStatusService
            .findAllWithPagination(PageRequest.of(page - 1, size), userId, mediaStatus)
            .map(mediaMapper::mapTo),
        HttpStatus.OK);
  }

  @GetMapping(params = {"type", "userId", "page", "size", "mediaStatus"})
  public ResponseEntity<Page<MediaDto>> getMediaStatusesByType(
      @RequestParam MediaType type,
      @RequestParam Long userId,
      @RequestParam Integer page,
      @RequestParam Integer size,
      @RequestParam MediaStatus mediaStatus) {

    return new ResponseEntity<>(
        mediaStatusService
            .findByTypeWithPagination(PageRequest.of(page - 1, size), type, userId, mediaStatus)
            .map(mediaMapper::mapTo),
        HttpStatus.OK);
  }

  @GetMapping(
      path = "/count",
      params = {"userId", "mediaStatus"})
  public ResponseEntity<Long> countAllByUserId(
      @RequestParam Long userId, @RequestParam MediaStatus mediaStatus) {
    Long count = mediaStatusService.countAllByUserIdAndStatus(userId, mediaStatus);
    return new ResponseEntity<>(count, HttpStatus.OK);
  }

  @GetMapping(
      path = "/count",
      params = {"type", "userId", "mediaStatus"})
  public ResponseEntity<Long> countByTypeAndUserId(
      @RequestParam MediaType type,
      @RequestParam Long userId,
      @RequestParam MediaStatus mediaStatus) {
    Long count = mediaStatusService.countByTypeAndUserIdAndStatus(type, userId, mediaStatus);
    return new ResponseEntity<>(count, HttpStatus.OK);
  }
}
