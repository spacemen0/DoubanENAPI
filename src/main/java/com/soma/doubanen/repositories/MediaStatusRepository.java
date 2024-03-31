package com.soma.doubanen.repositories;

import com.soma.doubanen.domains.entities.MediaStatusEntity;
import com.soma.doubanen.domains.enums.MediaStatus;
import com.soma.doubanen.domains.enums.MediaType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MediaStatusRepository extends JpaRepository<MediaStatusEntity, Long> {
  List<MediaStatusEntity> findByTypeAndUserIdAndStatus(
      MediaType type, Long userId, MediaStatus status);

  Optional<MediaStatusEntity> findOneByUserIdAndMediaId(Long userId, Long mediaId);

  @Query(
      "SELECT ms FROM MediaStatusEntity ms "
          + "WHERE ms.id = (SELECT mse.id FROM MediaStatusEntity mse "
          + "WHERE mse.type = :mediaType AND mse.userId = :userId "
          + "ORDER BY mse.date DESC, mse.id DESC LIMIT 1)")
  Optional<MediaStatusEntity> findUserLatestByType(MediaType mediaType, Long userId);

  Long countByTypeAndUserIdAndStatus(MediaType type, Long userId, MediaStatus status);

  Long countByUserIdAndStatus(Long userId, MediaStatus status);

  Page<MediaStatusEntity> findAllByUserIdAndTypeAndStatus(
      Pageable pageable, Long userId, MediaType type, MediaStatus status);

  Page<MediaStatusEntity> findAllByUserIdAndStatus(
      Pageable pageable, Long userId, MediaStatus status);

  @Transactional
  void deleteAllByUserId(Long userId);
}
