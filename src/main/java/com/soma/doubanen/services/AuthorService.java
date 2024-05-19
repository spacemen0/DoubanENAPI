package com.soma.doubanen.services;

import com.soma.doubanen.domains.entities.AuthorEntity;
import com.soma.doubanen.domains.entities.MediaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorService {
  AuthorEntity save(AuthorEntity authorEntity, Long id);

  Optional<AuthorEntity> findOne(Long id);

  List<AuthorEntity> findAll();

  Page<MediaEntity> findAllMediaWithPagination(Long id, Pageable pageable);

  Long countMedia(Long id);

  boolean notExists(Long id);

  void delete(Long id);

  AuthorEntity partialUpdate(Long id, AuthorEntity authorEntity);
}
