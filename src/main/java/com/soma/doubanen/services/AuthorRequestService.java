package com.soma.doubanen.services;

import com.soma.doubanen.domains.entities.AuthorEntity;
import com.soma.doubanen.domains.entities.AuthorRequestEntity;
import com.soma.doubanen.domains.enums.RequestStatus;
import java.util.List;
import java.util.Optional;

public interface AuthorRequestService {
  AuthorRequestEntity toAuthorRequest(AuthorEntity mediaEntity, RequestStatus status, Long userId);

  AuthorEntity toAuthor(AuthorRequestEntity mediaEntity);

  AuthorRequestEntity save(AuthorRequestEntity mediaRequestEntity);

  Optional<AuthorRequestEntity> findById(Long id);

  void delete(Long id);

  List<AuthorRequestEntity> findByUserId(Long userId);

  List<AuthorRequestEntity> findByStatus(RequestStatus status);
}
