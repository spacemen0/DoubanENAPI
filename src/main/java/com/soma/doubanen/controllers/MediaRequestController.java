package com.soma.doubanen.controllers;

import com.soma.doubanen.domains.dto.MediaDto;
import com.soma.doubanen.domains.dto.MediaRequestDto;
import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.entities.MediaRequestEntity;
import com.soma.doubanen.domains.enums.RequestStatus;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/media-requests")
public class MediaRequestController {

  private final MediaRequestService mediaRequestService;
  private final TokenService tokenService;
  private final UserService userService;

  private final ImageService imageService;

  private final MediaService mediaService;

  private final Mapper<MediaEntity, MediaDto> mediaMapper;

  private final Mapper<MediaRequestEntity, MediaRequestDto> mediaRequestMapper;

  public MediaRequestController(
      MediaRequestService mediaRequestService,
      TokenService tokenService,
      UserService userService,
      ImageService imageService,
      MediaService mediaService,
      Mapper<MediaEntity, MediaDto> mediaMapper,
      Mapper<MediaRequestEntity, MediaRequestDto> mediaRequestMapper) {
    this.mediaRequestService = mediaRequestService;
    this.tokenService = tokenService;
    this.userService = userService;
    this.imageService = imageService;
    this.mediaService = mediaService;
    this.mediaMapper = mediaMapper;
    this.mediaRequestMapper = mediaRequestMapper;
  }

  @GetMapping("/{id}")
  public ResponseEntity<MediaRequestEntity> getOne(@PathVariable("id") Long id) {
    Optional<MediaRequestEntity> optionalMediaRequestEntity = mediaRequestService.findById(id);
    return optionalMediaRequestEntity
        .map(mediaRequestEntity -> new ResponseEntity<>(mediaRequestEntity, HttpStatus.OK))
        .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
  }

  @PostMapping()
  public ResponseEntity<?> Create(
      @RequestBody MediaDto mediaDto,
      @RequestParam("status") RequestStatus status,
      @RequestParam("userId") Long userId,
      @RequestHeader(name = "Authorization") String auth) {
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(userId)))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    MediaRequestEntity mediaRequestEntity =
        mediaRequestService.toMediaRequest(mediaMapper.mapFrom(mediaDto), status, userId);
    return new ResponseEntity<>(mediaRequestService.save(mediaRequestEntity), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteOne(
      @PathVariable("id") Long id, @RequestHeader(name = "Authorization") String auth) {
    Optional<MediaRequestEntity> optionalMediaRequestEntity = mediaRequestService.findById(id);
    if (optionalMediaRequestEntity.isEmpty())
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(optionalMediaRequestEntity.get().getUserId())))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    mediaRequestService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping(params = "status")
  public ResponseEntity<?> getAllByStatus(@RequestParam("status") RequestStatus status) {
    return new ResponseEntity<>(
        mediaRequestService.findByStatus(status).stream()
            .map(mediaRequestMapper::mapTo)
            .collect(Collectors.toList()),
        HttpStatus.OK);
  }

  @GetMapping(params = "userId")
  public ResponseEntity<?> getAllByUserId(
      @RequestParam("userId") Long userId, @RequestHeader(name = "Authorization") String auth) {
    if (auth == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(userId)))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    return new ResponseEntity<>(
        mediaRequestService.findByUserId(userId).stream()
            .map(mediaRequestMapper::mapTo)
            .collect(Collectors.toList()),
        HttpStatus.OK);
  }

  @PutMapping(
      path = "/{id}",
      params = {"approve", "message"})
  public ResponseEntity<?> processRequest(
      @PathVariable Long id,
      @RequestParam("approve") boolean approve,
      @RequestParam("message") String message) {
    Optional<MediaRequestEntity> optionalMediaRequestEntity = mediaRequestService.findById(id);
    if (optionalMediaRequestEntity.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    MediaRequestEntity mediaRequestEntity = optionalMediaRequestEntity.get();
    if (mediaRequestEntity.getStatus() != RequestStatus.Pending)
      return new ResponseEntity<>("Request Status is not pending", HttpStatus.BAD_REQUEST);
    if (approve) {
      MediaEntity mediaEntity = mediaRequestService.toMedia(optionalMediaRequestEntity.get());
      Optional<MediaEntity> savedMediaEntity = mediaService.save(mediaEntity, null);
      if (savedMediaEntity.isPresent()) {
        mediaRequestEntity.setActionTime(LocalDateTime.now());
        mediaRequestEntity.setStatus(RequestStatus.Approved);
        mediaRequestEntity.setMessage(message);
        mediaRequestEntity.setResourceId(savedMediaEntity.get().getId());
        mediaRequestService.save(mediaRequestEntity);
        return new ResponseEntity<>(mediaMapper.mapTo(savedMediaEntity.get()), HttpStatus.OK);
      } else {
        return new ResponseEntity<>("Error saving media", HttpStatus.BAD_REQUEST);
      }
    } else {
      String imageUrl = mediaRequestEntity.getImageUrl();
      Long imageId = Long.valueOf(imageUrl.substring(8));
      imageService.deleteById(imageId);
      mediaRequestEntity.setActionTime(LocalDateTime.now());
      mediaRequestEntity.setStatus(RequestStatus.Rejected);
      mediaRequestEntity.setMessage(message);
      mediaRequestService.save(mediaRequestEntity);
      return new ResponseEntity<>(null, HttpStatus.OK);
    }
  }
}
