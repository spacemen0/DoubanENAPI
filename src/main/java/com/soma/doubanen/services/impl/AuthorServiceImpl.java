package com.soma.doubanen.services.impl;

import com.soma.doubanen.domains.entities.AuthorEntity;
import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.repositories.AuthorRepository;
import com.soma.doubanen.repositories.MediaRepository;
import com.soma.doubanen.services.AuthorService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl implements AuthorService {

  private final AuthorRepository authorRepository;

  private final MediaRepository mediaRepository;

  public AuthorServiceImpl(AuthorRepository authorRepository, MediaRepository mediaRepository) {
    this.authorRepository = authorRepository;
    this.mediaRepository = mediaRepository;
  }

  @Override
  public AuthorEntity save(AuthorEntity authorEntity, Long id) {
    authorEntity.setId(id);
    return authorRepository.save(authorEntity);
  }

  @Override
  public Optional<AuthorEntity> findOne(Long id) {
    return authorRepository.findById(id);
  }

  @Override
  public List<AuthorEntity> findAll() {
    return authorRepository.findAll();
  }

  @Override
  public boolean notExists(Long id) {
    return !authorRepository.existsById(id);
  }

  @Override
  public void delete(Long id) {
    Optional<AuthorEntity> artistOptional = authorRepository.findById(id);
    if (artistOptional.isPresent()) {
      List<MediaEntity> associatedMusic = mediaRepository.findByAuthorEntityId(id);
      for (MediaEntity mediaEntity : associatedMusic) {
        mediaRepository.deleteById(mediaEntity.getId());
      }
    }
    authorRepository.deleteById(id);
  }

  @Override
  public AuthorEntity partialUpdate(Long id, AuthorEntity authorEntity) {
    authorEntity.setId(id);
    return authorRepository
        .findById(id)
        .map(
            existingArtist -> {
              Optional.ofNullable(authorEntity.getGenres()).ifPresent(existingArtist::setGenres);
              Optional.ofNullable(authorEntity.getName()).ifPresent(existingArtist::setName);
              return existingArtist;
            })
        .orElseThrow(() -> new RuntimeException("Artist not fund"));
  }
}
