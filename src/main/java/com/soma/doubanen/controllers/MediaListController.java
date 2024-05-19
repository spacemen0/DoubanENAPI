package com.soma.doubanen.controllers;

import com.soma.doubanen.domains.dto.MediaDto;
import com.soma.doubanen.domains.dto.MediaListDto;
import com.soma.doubanen.domains.entities.ImageEntity;
import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.entities.MediaListEntity;
import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.domains.enums.ImageType;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.ImageService;
import com.soma.doubanen.services.MediaListService;
import com.soma.doubanen.services.TokenService;
import com.soma.doubanen.services.UserService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/media-lists")
public class MediaListController {

  private final MediaListService mediaListService;
  private final TokenService tokenService;
  private final UserService userService;

  private final ImageService imageService;
  private final Mapper<MediaListEntity, MediaListDto> mediaListMapper;

  private final Mapper<MediaEntity, MediaDto> mediaMapper;

  public MediaListController(
      MediaListService mediaListService,
      TokenService tokenService,
      UserService userService,
      ImageService imageService,
      Mapper<MediaListEntity, MediaListDto> mediaListMapper,
      Mapper<MediaEntity, MediaDto> mediaMapper) {
    this.mediaListService = mediaListService;
    this.tokenService = tokenService;
    this.userService = userService;
    this.imageService = imageService;
    this.mediaListMapper = mediaListMapper;
    this.mediaMapper = mediaMapper;
  }

  @PostMapping(consumes = {"multipart/form-data"})
  public ResponseEntity<?> createMediaList(
      @ModelAttribute MediaListDto mediaListDto,
      @RequestParam(value = "image", required = false) MultipartFile image,
      @RequestParam("userId") Long userId,
      @RequestHeader(name = "Authorization") String auth) {
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(userId)))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    Optional<UserEntity> user = userService.findOne(userId);
    if (user.isEmpty()) {
      return new ResponseEntity<>("Invalid User Id", HttpStatus.BAD_REQUEST);
    }
    MediaListEntity mediaList = mediaListMapper.mapFrom(mediaListDto);
    mediaList.setUserEntity(user.get());
    try {
      mediaList = mediaListService.save(mediaList, null);
    } catch (Exception e) {
      return (new ResponseEntity<>("Error save mediaList: " + e, HttpStatus.INTERNAL_SERVER_ERROR));
    }
    if (image != null) {
      if (FailedToSaveImageToList(image, mediaList))
        return new ResponseEntity<>("Error save image", HttpStatus.INTERNAL_SERVER_ERROR);
      try {
        MediaListEntity optionalMediaList = mediaListService.save(mediaList, mediaList.getId());
        return new ResponseEntity<>(mediaListMapper.mapTo(optionalMediaList), HttpStatus.CREATED);
      } catch (Exception e) {
        return (new ResponseEntity<>(
            "Error save image to list: " + e, HttpStatus.INTERNAL_SERVER_ERROR));
      }
    }
    return new ResponseEntity<>(mediaListMapper.mapTo(mediaList), HttpStatus.CREATED);
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity<MediaListDto> getMediaList(@PathVariable("id") Long id) {
    Optional<MediaListEntity> foundMediaList = mediaListService.findOne(id);
    return foundMediaList
        .map(
            mediaListEntity -> {
              MediaListDto mediaListDto = mediaListMapper.mapTo(mediaListEntity);
              return new ResponseEntity<>(mediaListDto, HttpStatus.OK);
            })
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping()
  public List<MediaListDto> listMediaLists() {
    List<MediaListEntity> mediaLists = mediaListService.findAll();
    return mediaLists.stream().map(mediaListMapper::mapTo).collect(Collectors.toList());
  }

  @PutMapping(
      path = "/{id}",
      consumes = {"multipart/form-data"})
  public ResponseEntity<MediaListDto> updateMediaList(
      @PathVariable("id") Long id,
      @ModelAttribute MediaListDto mediaListDto,
      @RequestParam("userId") Long userId,
      @RequestParam(value = "image", required = false) MultipartFile image,
      @RequestHeader(name = "Authorization") String auth) {
    Optional<MediaListEntity> mediaListEntity = mediaListService.findOne(id);
    if (mediaListEntity.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(userId)))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    Optional<UserEntity> user = userService.findOne(userId);
    if (user.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    MediaListEntity entity = mediaListMapper.mapFrom(mediaListDto);
    MediaListEntity result;
    entity.setUserEntity(user.get());
    entity.setImageUrl(mediaListEntity.get().getImageUrl());
    entity.setMediaEntities(mediaListEntity.get().getMediaEntities());
    try {
      result = mediaListService.save(entity, id);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    if (image != null) {
      if (FailedToSaveImageToList(image, result))
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
      try {
        MediaListEntity optionalMediaList = mediaListService.save(result, result.getId());
        return new ResponseEntity<>(mediaListMapper.mapTo(optionalMediaList), HttpStatus.CREATED);
      } catch (Exception e) {
        return (new ResponseEntity<>(HttpStatus.BAD_REQUEST));
      }
    } else {
      return new ResponseEntity<>(mediaListMapper.mapTo(result), HttpStatus.CREATED);
    }
  }

  private boolean FailedToSaveImageToList(
      @RequestParam(value = "image", required = false) MultipartFile image,
      MediaListEntity result) {
    byte[] data;
    try {
      data = imageService.compressImage(image.getBytes());
    } catch (IOException e) {
      return true;
    }
    ImageEntity imageEntity =
        ImageEntity.builder()
            .imageData(data)
            .objectId(result.getId())
            .type(ImageType.MediaListCover)
            .build();
    ImageEntity savedImage = imageService.save(imageEntity);
    result.setImageUrl("/images/" + savedImage.getId());
    return false;
  }

  @PatchMapping(path = "/{id}")
  public ResponseEntity<MediaListDto> partialUpdateMediaList(
      @PathVariable("id") Long id,
      @RequestBody MediaListDto mediaListDto,
      @RequestHeader(name = "Authorization") String auth) {
    if (mediaListService.notExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(mediaListDto.getUser().getId())))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    MediaListEntity mediaListEntity = mediaListMapper.mapFrom(mediaListDto);
    MediaListEntity updatedMediaList = mediaListService.partialUpdate(id, mediaListEntity);
    return new ResponseEntity<>(mediaListMapper.mapTo(updatedMediaList), HttpStatus.OK);
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deleteMediaList(
      @PathVariable("id") Long id, @RequestHeader(name = "Authorization") String auth) {
    Optional<MediaListEntity> mediaListEntity = mediaListService.findOne(id);
    if (mediaListEntity.isPresent()) {
      String token = auth.substring(7);
      String username = tokenService.extractUsername(token);
      if (!username.equals(
          userService.getUsernameById(mediaListEntity.get().getUserEntity().getId())))
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    mediaListService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping(path = "/{id}/count")
  public ResponseEntity<Long> getItemsCountById(@PathVariable("id") Long id) {
    return new ResponseEntity<>(mediaListService.countById(id), HttpStatus.OK);
  }

  @GetMapping(
      path = "/{id}",
      params = {"page", "size"})
  public ResponseEntity<List<MediaDto>> getMediaEntitiesWithPagination(
      @PathVariable("id") Long id, @RequestParam Long page, @RequestParam Long size) {
    Optional<MediaListEntity> foundMediaList = mediaListService.findOne(id);
    return foundMediaList
        .map(
            mediaListEntity ->
                new ResponseEntity<>(
                    mediaListEntity.getMediaEntities().stream()
                        .skip((page - 1) * size)
                        .limit(size)
                        .map(mediaMapper::mapTo)
                        .collect(Collectors.toList()),
                    HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping(params = "userId")
  public List<MediaListDto> getUserLists(@RequestParam Long userId) {
    return mediaListService.findByUserId(userId).stream()
        .map(mediaListMapper::mapTo)
        .collect(Collectors.toList());
  }

  @PostMapping(path = "/{id}/add-media", params = "mediaId")
  public ResponseEntity<Void> addMediaToList(
      @PathVariable("id") Long id,
      @RequestParam Long mediaId,
      @RequestHeader(name = "Authorization") String auth) {
    Optional<MediaListEntity> mediaListEntity = mediaListService.findOne(id);
    if (mediaListEntity.isPresent()) {
      String token = auth.substring(7);
      String username = tokenService.extractUsername(token);
      if (!username.equals(
          userService.getUsernameById(mediaListEntity.get().getUserEntity().getId())))
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
      MediaListEntity mediaList = mediaListEntity.get();
      List<MediaEntity> mediaEntities = mediaList.getMediaEntities();
      if (mediaEntities.stream().anyMatch(mediaEntity -> mediaEntity.getId().equals(mediaId)))
        return new ResponseEntity<>(HttpStatus.CONFLICT);
      Boolean flag = mediaListService.addMediaToList(id, mediaId);
      if (flag) return new ResponseEntity<>(HttpStatus.CREATED);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  @DeleteMapping(path = "/{id}/delete-media", params = "mediaId")
  public ResponseEntity<Void> removeMediaToList(
      @PathVariable("id") Long id,
      @RequestParam Long mediaId,
      @RequestHeader(name = "Authorization") String auth) {
    Optional<MediaListEntity> mediaListEntity = mediaListService.findOne(id);
    if (mediaListEntity.isPresent()) {
      String token = auth.substring(7);
      String username = tokenService.extractUsername(token);
      if (!username.equals(
          userService.getUsernameById(mediaListEntity.get().getUserEntity().getId())))
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    Boolean flag = mediaListService.removeMediaToList(id, mediaId);
    if (flag) return new ResponseEntity<>(HttpStatus.CREATED);
    else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }
}
