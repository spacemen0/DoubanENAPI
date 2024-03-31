package com.soma.doubanen.controllers;

import com.soma.doubanen.domains.dto.AuthorDto;
import com.soma.doubanen.domains.entities.AuthorEntity;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.AuthorService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthorController {

  private final AuthorService authorService;
  private final Mapper<AuthorEntity, AuthorDto> artistMapper;

  public AuthorController(
      AuthorService authorService, Mapper<AuthorEntity, AuthorDto> artistMapper) {
    this.authorService = authorService;
    this.artistMapper = artistMapper;
  }

  @PostMapping(path = "/authors")
  public ResponseEntity<AuthorDto> createArtist(@RequestBody AuthorDto authorDto) {
    AuthorEntity authorEntity = artistMapper.mapFrom(authorDto);
    AuthorEntity savedAuthorEntity = authorService.save(authorEntity, null);
    return new ResponseEntity<>(artistMapper.mapTo(savedAuthorEntity), HttpStatus.CREATED);
  }

  @GetMapping(path = "/authors")
  public List<AuthorDto> listArtist() {
    List<AuthorEntity> artistEntities = authorService.findAll();
    return artistEntities.stream().map(artistMapper::mapTo).collect(Collectors.toList());
  }

  @GetMapping(path = "/authors/{id}")
  public ResponseEntity<AuthorDto> getArtist(@PathVariable("id") Long id) {
    Optional<AuthorEntity> foundArtist = authorService.findOne(id);
    return foundArtist
        .map(artistEntity -> new ResponseEntity<>(artistMapper.mapTo(artistEntity), HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PutMapping(path = "/authors/{id}")
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

  @PatchMapping(path = "/authors/{id}")
  public ResponseEntity<AuthorDto> partialUpdate(
      @PathVariable("id") Long id, @RequestBody AuthorDto authorDto) {
    if (authorService.notExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    AuthorEntity authorEntity = artistMapper.mapFrom(authorDto);
    AuthorEntity updatedArtist = authorService.partialUpdate(id, authorEntity);
    return new ResponseEntity<>(artistMapper.mapTo(updatedArtist), HttpStatus.OK);
  }

  @DeleteMapping(path = "/authors/{id}")
  public ResponseEntity<Void> deleteArtist(@PathVariable("id") Long id) {
    authorService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
