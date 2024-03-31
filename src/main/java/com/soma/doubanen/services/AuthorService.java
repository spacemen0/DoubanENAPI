package com.soma.doubanen.services;

import com.soma.doubanen.domains.entities.AuthorEntity;
import java.util.List;
import java.util.Optional;

public interface AuthorService {
  AuthorEntity save(AuthorEntity authorEntity, Long id);

  Optional<AuthorEntity> findOne(Long id);

  List<AuthorEntity> findAll();

  boolean notExists(Long id);

  void delete(Long id);

  AuthorEntity partialUpdate(Long id, AuthorEntity authorEntity);
}
