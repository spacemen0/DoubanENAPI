package com.soma.doubanen.services.impl;

import com.soma.doubanen.domains.entities.AuthorEntity;
import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.enums.MediaType;
import com.soma.doubanen.repositories.AuthorRepository;
import com.soma.doubanen.repositories.MediaRepository;
import com.soma.doubanen.services.MediaService;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MediaServiceImpl implements MediaService {

  private final MediaRepository mediaRepository;

  private final AuthorRepository authorRepository;

  public MediaServiceImpl(MediaRepository mediaRepository, AuthorRepository authorRepository) {
    this.mediaRepository = mediaRepository;
    this.authorRepository = authorRepository;
  }

  // could reject when artist id not found in table or ignore the passed id and created a new artist
  // use with id set to null when creating
  // only accept null artist id or id of existed artist
  @Override
  public Optional<MediaEntity> save(MediaEntity mediaEntity, Long id) {
    mediaEntity.setId(id);
    if (mediaEntity.getAuthorEntity() != null) {
      if (mediaEntity.getAuthorEntity().getId() == null) {
        AuthorEntity savedArtist = authorRepository.save(mediaEntity.getAuthorEntity());
        mediaEntity.setAuthorEntity(savedArtist);
      } else {
        if (!authorRepository.existsById(mediaEntity.getAuthorEntity().getId())) {
          return Optional.empty();
        } else {
          AuthorEntity existedArtist =
              authorRepository.findById(mediaEntity.getAuthorEntity().getId()).orElseThrow();
          mediaEntity.setAuthorEntity(existedArtist);
        }
      }
    }
    return Optional.of(mediaRepository.save(mediaEntity));
  }

  @Override
  public Optional<MediaEntity> findOne(Long id) {
    return mediaRepository.findById(id);
  }

  @Override
  public List<MediaEntity> findAll() {
    return mediaRepository.findAll();
  }

  public Long countAll() {
    return mediaRepository.count();
  }

  public Long countByType(MediaType mediaType) {
    return mediaRepository.countByType(mediaType);
  }

  @Override
  public Page<MediaEntity> findAll(Pageable pageable) {
    return mediaRepository.findAll(pageable);
  }

  @Override
  public Page<MediaEntity> findAll(Pageable pageable, MediaType mediaType) {
    return mediaRepository.findByType(mediaType, pageable);
  }

  @Override
  public boolean notExists(Long id) {
    return !mediaRepository.existsById(id);
  }

  @Override
  public void delete(Long id) {
    mediaRepository.deleteById(id);
  }

  @Override
  public MediaEntity partialUpdate(Long id, MediaEntity mediaEntity) {
    return mediaRepository
        .findById(id)
        .map(
            existingMedia -> {
              if (mediaEntity.getAuthorEntity() != null)
                existingMedia.setAuthorEntity(mediaEntity.getAuthorEntity());
              if (mediaEntity.getTitle() != null) existingMedia.setTitle(mediaEntity.getTitle());
              if (mediaEntity.getDescription() != null)
                existingMedia.setDescription(mediaEntity.getDescription());
              if (mediaEntity.getAdditionalInfo() != null)
                existingMedia.setAdditionalInfo(mediaEntity.getAdditionalInfo());
              if (mediaEntity.getReleaseDate() != null)
                existingMedia.setReleaseDate(mediaEntity.getReleaseDate());
              if (mediaEntity.getAverage() != null)
                existingMedia.setAverage(mediaEntity.getAverage());
              if (mediaEntity.getRatings() != null)
                existingMedia.setRatings(mediaEntity.getRatings());
              if (mediaEntity.getWants() != null) existingMedia.setWants(mediaEntity.getWants());
              if (mediaEntity.getGenre() != null) existingMedia.setGenre(mediaEntity.getGenre());
              if (mediaEntity.getType() != null) existingMedia.setType(mediaEntity.getType());
              if (mediaEntity.getImageUrl() != null)
                existingMedia.setImageUrl(mediaEntity.getImageUrl());
              return mediaRepository.save(existingMedia);
            })
        .orElseThrow(() -> new RuntimeException("Media does not exist"));
  }
}
