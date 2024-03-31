package com.soma.doubanen.services;

import com.soma.doubanen.domains.entities.ReviewEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
  ReviewEntity save(ReviewEntity reviewEntity, Long id) throws Exception;

  Optional<ReviewEntity> findOne(Long id);

  Page<ReviewEntity> findAllByMediaId(Long mediaId, Pageable pageable);

  void deleteByUserIdAndMediaId(Long userId, Long mediaId);

  Long countAllByMediaId(Long mediaId);

  boolean notExists(Long id);

  void delete(Long id);

  ReviewEntity partialUpdate(Long id, ReviewEntity reviewEntity);
}
