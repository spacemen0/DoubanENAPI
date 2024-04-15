package com.soma.doubanen.repositories;

import com.soma.doubanen.domains.entities.ReviewEntity;
import com.soma.doubanen.domains.enums.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
  long countByMediaId(Long mediaId);

  long countByUserIdAndType(Long user_id, MediaType mediaType);

  @Transactional
  void deleteByUserIdAndMediaId(Long userId, Long mediaId);

  Page<ReviewEntity> findByMediaId(Long mediaId, Pageable pageable);

  Page<ReviewEntity> findByUserIdAndType(Long user_id, MediaType mediaType, Pageable pageable);
}
