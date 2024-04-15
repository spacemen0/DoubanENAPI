package com.soma.doubanen.services;

import com.soma.doubanen.domains.entities.ReviewEntity;
import com.soma.doubanen.domains.enums.MediaType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
  ReviewEntity save(ReviewEntity reviewEntity, Long id) throws Exception;

  Optional<ReviewEntity> findOne(Long id);

  Page<ReviewEntity> findAllByMediaId(Long mediaId, Pageable pageable);

  Page<ReviewEntity> findAllByUserIdAndType(Long userId, MediaType type, Pageable pageable);

  void deleteByUserIdAndMediaId(Long userId, Long mediaId);

  Long countAllByMediaId(Long mediaId);

  Long countAllByUserIdAndMediaType(Long userId, MediaType type);

  boolean notExists(Long id);

  void delete(Long id);

  ReviewEntity partialUpdate(Long id, ReviewEntity reviewEntity);
}
