package com.soma.doubanen.services;

import com.soma.doubanen.domains.entities.CommentEntity;
import com.soma.doubanen.domains.enums.CommentArea;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
  CommentEntity save(Long id, CommentEntity commentEntity);

  Optional<CommentEntity> findById(Long id);

  Page<CommentEntity> findAllByCommentAreaAndAreaId(Long id, CommentArea area, Pageable pageable);

  Page<CommentEntity> findAllByUserId(Long userId, Pageable pageable);

  Long countByUserId(Long userId);

  Long countByCommentAreaAndAreaId(CommentArea area, Long areaId);

  void delete(Long id);
}
