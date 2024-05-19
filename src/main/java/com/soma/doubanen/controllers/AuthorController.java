package com.soma.doubanen.controllers;

import com.soma.doubanen.domains.dto.AuthorDto;
import com.soma.doubanen.domains.dto.MediaDto;
import com.soma.doubanen.domains.entities.AuthorEntity;
import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.domains.enums.RequestStatus;
import com.soma.doubanen.domains.enums.UserRole;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.AuthorRequestService;
import com.soma.doubanen.services.AuthorService;
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
@RequestMapping("/authors")
public class AuthorController {

  private final AuthorService authorService;
  private final Mapper<AuthorEntity, AuthorDto> artistMapper;

  private final Mapper<MediaEntity, MediaDto> mediaMapper;

  private final TokenService tokenService;

  private final AuthorRequestService authorRequestService;

  private final UserService userService;

  public AuthorController(
      AuthorService authorService,
      Mapper<AuthorEntity, AuthorDto> artistMapper,
      Mapper<MediaEntity, MediaDto> mediaMapper,
      TokenService tokenService,
      AuthorRequestService authorRequestService,
      UserService userService) {
    this.authorService = authorService;
    this.artistMapper = artistMapper;
    this.mediaMapper = mediaMapper;
    this.tokenService = tokenService;
    this.authorRequestService = authorRequestService;
    this.userService = userService;
  }

  @PostMapping()
  public ResponseEntity<?> createArtist(
      @RequestBody AuthorDto authorDto, @RequestHeader(name = "Authorization") String auth) {
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    Optional<UserEntity> userEntity = userService.findByUsername(username);
    if (userEntity.isEmpty()) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    UserEntity user = userEntity.get();
    AuthorEntity authorEntity = artistMapper.mapFrom(authorDto);
    authorEntity.setId(null);
    if (user.getRole() == UserRole.Admin) {
      AuthorEntity savedAuthorEntity = authorService.save(authorEntity, null);
      return new ResponseEntity<>(artistMapper.mapTo(savedAuthorEntity), HttpStatus.CREATED);
    } else {
      authorRequestService.save(
          authorRequestService.toAuthorRequest(authorEntity, RequestStatus.Pending, user.getId()));
      return new ResponseEntity<>("Request Sent", HttpStatus.OK);
    }
  }

  @GetMapping()
  public List<AuthorDto> listArtist() {
    List<AuthorEntity> artistEntities = authorService.findAll();
    return artistEntities.stream().map(artistMapper::mapTo).collect(Collectors.toList());
  }

  @GetMapping(path = "/{id}")
  public ResponseEntity<AuthorDto> getArtist(@PathVariable("id") Long id) {
    Optional<AuthorEntity> foundArtist = authorService.findOne(id);
    return foundArtist
        .map(artistEntity -> new ResponseEntity<>(artistMapper.mapTo(artistEntity), HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PutMapping(path = "/{id}")
  public ResponseEntity<AuthorDto> updateMusic(
      @PathVariable("id") Long id, @RequestBody AuthorDto authorDto) {
    if (authorService.notExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    authorDto.setId(id);
    AuthorEntity authorEntity = artistMapper.mapFrom(authorDto);
    AuthorEntity savedAuthorEntity = authorService.save(authorEntity, id);
    return new ResponseEntity<>(artistMapper.mapTo(savedAuthorEntity), HttpStatus.OK);
  }

  @PatchMapping(path = "/{id}")
  public ResponseEntity<AuthorDto> partialUpdate(
      @PathVariable("id") Long id, @RequestBody AuthorDto authorDto) {
    if (authorService.notExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    AuthorEntity authorEntity = artistMapper.mapFrom(authorDto);
    AuthorEntity updatedArtist = authorService.partialUpdate(id, authorEntity);
    return new ResponseEntity<>(artistMapper.mapTo(updatedArtist), HttpStatus.OK);
  }

  @DeleteMapping(path = "/{id}")
  public ResponseEntity<Void> deleteArtist(@PathVariable("id") Long id) {
    authorService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping(
      path = "/{id}/media",
      params = {"page", "size"})
  public Page<MediaDto> listMediaWithPagination(
      @PathVariable Long id, @RequestParam int page, @RequestParam int size) {
    Page<MediaEntity> mediaEntities =
        authorService.findAllMediaWithPagination(id, PageRequest.of(page - 1, size));
    return mediaEntities.map(mediaMapper::mapTo);
  }

  @GetMapping("/{id}/count")
  public ResponseEntity<?> getMediaCount(@PathVariable Long id) {
    if (authorService.notExists(id)) return new ResponseEntity<>("Not exist", HttpStatus.NOT_FOUND);
    else return new ResponseEntity<>(authorService.countMedia(id), HttpStatus.OK);
  }
}
