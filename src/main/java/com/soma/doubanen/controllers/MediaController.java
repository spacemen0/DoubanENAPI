package com.soma.doubanen.controllers;

import com.soma.doubanen.domains.dto.MediaDto;
import com.soma.doubanen.domains.dto.SearchRequestDto;
import com.soma.doubanen.domains.entities.*;
import com.soma.doubanen.domains.enums.*;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
public class MediaController {

  private final MediaService mediaService;

  private final ImageService imageService;

  private final AuthorService authorService;

  private final TokenService tokenService;

  private final UserService userService;

  private final MediaStatusService mediaStatusService;

  private final MediaRequestService mediaRequestService;

  private final Mapper<MediaEntity, MediaDto> mediaMapper;

  public MediaController(
      MediaService mediaService,
      ImageService imageService,
      AuthorService authorService,
      TokenService tokenService,
      UserService userService,
      MediaStatusService mediaStatusService,
      MediaRequestService mediaRequestService,
      Mapper<MediaEntity, MediaDto> mediaMapper) {
    this.mediaService = mediaService;
    this.imageService = imageService;
    this.authorService = authorService;
    this.tokenService = tokenService;
    this.userService = userService;
    this.mediaStatusService = mediaStatusService;
    this.mediaRequestService = mediaRequestService;
    this.mediaMapper = mediaMapper;
  }

  @PostMapping(consumes = {"multipart/form-data"})
  public ResponseEntity<?> createMedia(
      @ModelAttribute MediaDto mediaDto,
      @RequestParam(value = "image") MultipartFile image,
      @RequestParam(value = "authorName", required = false) String authorName,
      @RequestParam(value = "authorType", required = false) AuthorType authorType,
      @RequestParam(value = "authorGenres", required = false) List<MediaGenre> genres,
      @RequestParam(value = "authorId", required = false) Long authorId,
      @RequestHeader(name = "Authorization") String auth) {
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    Optional<UserEntity> userEntity = userService.findByUsername(username);
    if (userEntity.isEmpty()) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    UserEntity user = userEntity.get();
    MediaEntity mediaEntity = mediaMapper.mapFrom(mediaDto);
    mediaEntity.setAuthorEntity(null);
    byte[] data;
    try {
      data = imageService.compressImage(image.getBytes());
    } catch (IOException e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    ImageEntity imageEntity =
        ImageEntity.builder()
            .imageData(data)
            .objectId(-1L) // placeholder
            .type(ImageType.MediaArt)
            .build();
    ImageEntity savedImage = imageService.save(imageEntity);
    mediaEntity.setImageUrl("/images/" + savedImage.getId());
    AuthorEntity author;
    if (authorId == null) {
      if (authorName != null && authorType != null) {
        author = AuthorEntity.builder().type(authorType).name(authorName).build();
        if (genres != null) author.setGenres(genres);
      } else {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
    } else {
      Optional<AuthorEntity> optionalAuthor = authorService.findOne(authorId);
      if (optionalAuthor.isEmpty()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      author = optionalAuthor.get();
    }
    mediaEntity.setAuthorEntity(author);
    if (!mediaService.check(mediaEntity, null)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    if (user.getRole() == UserRole.Admin) {
      Optional<MediaEntity> optionalMedia =
          mediaService.save(mediaEntity, null); // author will also be persisted
      return optionalMedia
          .map(
              addedMedia -> {
                MediaDto addedMediaDto = mediaMapper.mapTo(addedMedia);
                return new ResponseEntity<>(addedMediaDto, HttpStatus.CREATED);
              })
          .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    } else {
      mediaRequestService.save(
          mediaRequestService.toMediaRequest(mediaEntity, RequestStatus.Pending, user.getId()));
      return new ResponseEntity<>("Request Sent", HttpStatus.OK);
    }
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

  @GetMapping(
      params = {
        "userId",
        "mediaType",
        "mediaStatus",
        "page",
        "size",
      })
  public Page<MediaDto> getCorrespondingMediasByUserMediaStatuses(
      @RequestParam Long userId,
      @RequestParam MediaType mediaType,
      @RequestParam MediaStatus mediaStatus,
      @RequestParam Integer page,
      @RequestParam Integer size) {
    Page<MediaStatusEntity> mediaStatuses =
        mediaStatusService.findByTypeAndUserIdAndStatus(
            mediaType, userId, mediaStatus, PageRequest.of(page - 1, size));

    return mediaStatuses.map(
        (correspondingMediaStatus) -> {
          Optional<MediaEntity> mediaEntity =
              mediaService.findOne(correspondingMediaStatus.getMediaId());
          return mediaEntity.map(mediaMapper::mapTo).orElse(null);
        });
  }

  @GetMapping(params = "userId")
  public List<MediaDto> getUserCurrentEngagingMedias(@RequestParam Long userId) {
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
  public ResponseEntity<Page<MediaDto>> getAllCorrespondingMediasByUserMediaStatus(
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
  public ResponseEntity<Page<MediaDto>> getCorrespondingMediasByUserMediaStatusAndMediaType(
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

  @GetMapping(path = "/search")
  public List<MediaDto> searchMedias(SearchRequestDto searchRequestDto) {
    return mediaService
        .searchMedias(
            searchRequestDto.getText(),
            searchRequestDto.getFields(),
            searchRequestDto.getPage(),
            searchRequestDto.getLimit(),
            searchRequestDto.getType())
        .stream()
        .map(mediaMapper::mapTo)
        .collect(Collectors.toList());
  }
}
