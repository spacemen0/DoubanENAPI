package com.soma.doubanen.services;

import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.entities.MediaStatusEntity;
import com.soma.doubanen.domains.enums.MediaStatus;
import com.soma.doubanen.domains.enums.MediaType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MediaStatusService {
  Optional<MediaStatusEntity> save(MediaStatusEntity mediaStatusEntity, Long id);

  Optional<MediaStatusEntity> findOne(Long id);

  List<MediaStatusEntity> findAll();

  Page<MediaEntity> findAllWithPagination(Pageable pageable, Long userId, MediaStatus status);

  Page<MediaEntity> findByTypeWithPagination(
      Pageable pageable, MediaType type, Long userId, MediaStatus status);

  Long countAllByUserIdAndStatus(Long userId, MediaStatus status);

  Long countByTypeAndUserIdAndStatus(MediaType type, Long userId, MediaStatus status);

  Page<MediaStatusEntity> findByTypeAndUserIdAndStatus(
      MediaType type, Long userId, MediaStatus status, Pageable pageable);

  Optional<MediaStatusEntity> findByUserIdAndMediaId(Long userId, Long mediaId);

  List<MediaStatusEntity> getUserCurrentOn(Long userId);

  boolean notExists(Long id);

  void delete(Long id);

  MediaStatusEntity partialUpdate(Long id, MediaStatusEntity mediaStatusEntity);
}
