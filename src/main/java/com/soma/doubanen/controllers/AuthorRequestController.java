package com.soma.doubanen.controllers;

import com.soma.doubanen.domains.dto.AuthorDto;
import com.soma.doubanen.domains.dto.AuthorRequestDto;
import com.soma.doubanen.domains.entities.AuthorEntity;
import com.soma.doubanen.domains.entities.AuthorRequestEntity;
import com.soma.doubanen.domains.enums.RequestStatus;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.AuthorRequestService;
import com.soma.doubanen.services.AuthorService;
import com.soma.doubanen.services.TokenService;
import com.soma.doubanen.services.UserService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/author-requests")
public class AuthorRequestController {

  private final AuthorRequestService authorRequestService;

  private final AuthorService authorService;
  private final TokenService tokenService;
  private final UserService userService;

  private final Mapper<AuthorEntity, AuthorDto> authorMapper;

  private final Mapper<AuthorRequestEntity, AuthorRequestDto> authorRequestMapper;

  public AuthorRequestController(
      AuthorRequestService authorRequestService,
      AuthorService authorService,
      TokenService tokenService,
      UserService userService,
      Mapper<AuthorEntity, AuthorDto> authorMapper,
      Mapper<AuthorRequestEntity, AuthorRequestDto> authorRequestMapper) {
    this.authorRequestService = authorRequestService;
    this.authorService = authorService;
    this.tokenService = tokenService;
    this.userService = userService;
    this.authorMapper = authorMapper;
    this.authorRequestMapper = authorRequestMapper;
  }

  @GetMapping("/{id}")
  public ResponseEntity<AuthorRequestEntity> getOne(@PathVariable("id") Long id) {
    Optional<AuthorRequestEntity> optionalAuthorRequestEntity = authorRequestService.findById(id);
    return optionalAuthorRequestEntity
        .map(authorRequestEntity -> new ResponseEntity<>(authorRequestEntity, HttpStatus.OK))
        .orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
  }

  @PostMapping()
  public ResponseEntity<?> Create(
      @RequestBody AuthorDto authorDto,
      @RequestParam("status") RequestStatus status,
      @RequestParam("userId") Long userId,
      @RequestHeader(name = "Authorization") String auth) {
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(userId)))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    AuthorRequestEntity authorRequestEntity =
        authorRequestService.toAuthorRequest(authorMapper.mapFrom(authorDto), status, userId);
    return new ResponseEntity<>(authorRequestService.save(authorRequestEntity), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteOne(
      @PathVariable("id") Long id, @RequestHeader(name = "Authorization") String auth) {
    Optional<AuthorRequestEntity> optionalAuthorRequestEntity = authorRequestService.findById(id);
    if (optionalAuthorRequestEntity.isEmpty())
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(
        userService.getUsernameById(optionalAuthorRequestEntity.get().getUserId())))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    authorRequestService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping(params = "status")
  public ResponseEntity<?> getAllByStatus(@RequestParam("status") RequestStatus status) {
    return new ResponseEntity<>(
        authorRequestService.findByStatus(status).stream()
            .map(authorRequestMapper::mapTo)
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
        authorRequestService.findByUserId(userId).stream()
            .map(authorRequestMapper::mapTo)
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
    Optional<AuthorRequestEntity> optionalAuthorRequestEntity = authorRequestService.findById(id);
    if (optionalAuthorRequestEntity.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    AuthorRequestEntity authorRequestEntity = optionalAuthorRequestEntity.get();
    if (authorRequestEntity.getStatus() != RequestStatus.Pending)
      return new ResponseEntity<>("Request Status is not pending", HttpStatus.BAD_REQUEST);

    if (approve) {
      AuthorEntity savedAuthorEntity =
          authorService.save(authorRequestService.toAuthor(authorRequestEntity), null);
      authorRequestEntity.setActionTime(LocalDateTime.now());
      authorRequestEntity.setStatus(RequestStatus.Approved);
      authorRequestEntity.setMessage(message);
      authorRequestEntity.setResourceId(savedAuthorEntity.getId());
      authorRequestService.save(authorRequestEntity);
      return new ResponseEntity<>(authorMapper.mapTo(savedAuthorEntity), HttpStatus.OK);
    } else {
      authorRequestEntity.setActionTime(LocalDateTime.now());
      authorRequestEntity.setStatus(RequestStatus.Rejected);
      authorRequestEntity.setMessage(message);
      authorRequestService.save(authorRequestEntity);
      return new ResponseEntity<>(null, HttpStatus.OK);
    }
  }
}
