package com.soma.doubanen.services.impl;

import com.soma.doubanen.domains.entities.AuthorEntity;
import com.soma.doubanen.domains.entities.AuthorRequestEntity;
import com.soma.doubanen.domains.enums.AuthorType;
import com.soma.doubanen.domains.enums.MediaGenre;
import com.soma.doubanen.domains.enums.RequestStatus;
import com.soma.doubanen.repositories.AuthorRequestRepository;
import com.soma.doubanen.services.AuthorRequestService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AuthorRequestServiceImpl implements AuthorRequestService {
  private final AuthorRequestRepository authorRequestRepository;

  public AuthorRequestServiceImpl(AuthorRequestRepository authorRequestRepository) {
    this.authorRequestRepository = authorRequestRepository;
  }

  @Override
  public AuthorRequestEntity toAuthorRequest(
      AuthorEntity authorEntity, RequestStatus status, Long userId) {

    AuthorRequestEntity authorRequestEntity = new AuthorRequestEntity();

    copyFields(
        authorRequestEntity,
        authorEntity.getName(),
        authorEntity.getType(),
        authorEntity.getGenres(),
        null);

    authorRequestEntity.setActionTime(LocalDateTime.now());
    authorRequestEntity.setStatus(status);
    authorRequestEntity.setUserId(userId);

    return authorRequestEntity;
  }

  @Override
  public AuthorEntity toAuthor(AuthorRequestEntity authorRequestEntity) {
    AuthorEntity authorEntity = new AuthorEntity();

    copyFields(
        null,
        authorRequestEntity.getName(),
        authorRequestEntity.getType(),
        authorRequestEntity.getGenres(),
        authorEntity);

    return authorEntity;
  }

  private void copyFields(
      AuthorRequestEntity authorRequestEntity,
      String name,
      AuthorType type,
      List<MediaGenre> genres,
      AuthorEntity authorEntity) {
    if (authorRequestEntity != null) {
      authorRequestEntity.setName(name);
      authorRequestEntity.setType(type);
      authorRequestEntity.setGenres(genres);
    }
    if (authorEntity != null) {
      authorEntity.setGenres(genres);
      authorEntity.setType(type);
      authorEntity.setName(name);
    }
  }

  @Override
  public AuthorRequestEntity save(AuthorRequestEntity authorRequestEntity) {
    return authorRequestRepository.save(authorRequestEntity);
  }

  @Override
  public Optional<AuthorRequestEntity> findById(Long id) {
    return authorRequestRepository.findById(id);
  }

  @Override
  public void delete(Long id) {
    authorRequestRepository.deleteById(id);
  }

  @Override
  public List<AuthorRequestEntity> findByUserId(Long userId) {
    return authorRequestRepository.findByUserId(userId);
  }

  @Override
  public List<AuthorRequestEntity> findByStatus(RequestStatus status) {
    return authorRequestRepository.findByStatus(status);
  }
}
