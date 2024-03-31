package com.soma.doubanen.controllers;

import com.soma.doubanen.domains.dto.MediaDto;
import com.soma.doubanen.domains.dto.MediaListDto;
import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.entities.MediaListEntity;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.MediaListService;
import com.soma.doubanen.services.TokenService;
import com.soma.doubanen.services.UserService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/media-lists")
public class MediaListController {

  private final MediaListService mediaListService;
  private final TokenService tokenService;
  private final UserService userService;
  private final Mapper<MediaListEntity, MediaListDto> mediaListMapper;

  private final Mapper<MediaEntity, MediaDto> mediaMapper;

  public MediaListController(
      MediaListService mediaListService,
      TokenService tokenService,
      UserService userService,
      Mapper<MediaListEntity, MediaListDto> mediaListMapper,
      Mapper<MediaEntity, MediaDto> mediaMapper) {
    this.mediaListService = mediaListService;
    this.tokenService = tokenService;
    this.userService = userService;
    this.mediaListMapper = mediaListMapper;
    this.mediaMapper = mediaMapper;
  }

  @PostMapping()
  public ResponseEntity<MediaListDto> createMediaList(
      @RequestBody MediaListDto mediaListDto, @RequestHeader(name = "Authorization") String auth)
      throws Exception {
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(mediaListDto.getUser().getId())))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    MediaListEntity mediaList = mediaListMapper.mapFrom(mediaListDto);
    Optional<MediaListEntity> result = mediaListService.save(mediaList, null);
    return result
        .map(
            mediaListEntity ->
                new ResponseEntity<>(mediaListMapper.mapTo(mediaListEntity), HttpStatus.CREATED))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
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

  @PutMapping(path = "/{id}")
  public ResponseEntity<MediaListDto> updateMediaList(
      @PathVariable("id") Long id,
      @RequestBody MediaListDto mediaListDto,
      @RequestHeader(name = "Authorization") String auth)
      throws Exception {
    if (mediaListService.notExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(mediaListDto.getUser().getId())))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    MediaListEntity entity = mediaListMapper.mapFrom(mediaListDto);
    Optional<MediaListEntity> result = mediaListService.save(entity, id);
    return result
        .map(
            mediaListEntity ->
                new ResponseEntity<>(mediaListMapper.mapTo(mediaListEntity), HttpStatus.CREATED))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
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
    }
    Boolean flag = mediaListService.addMediaToList(id, mediaId);
    if (flag) return new ResponseEntity<>(HttpStatus.CREATED);
    else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }
}
