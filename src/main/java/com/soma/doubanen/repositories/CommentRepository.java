package com.soma.doubanen.repositories;

import com.soma.doubanen.domains.entities.CommentEntity;
import com.soma.doubanen.domains.enums.CommentArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
  Page<CommentEntity> findAllByUserId(Long userId, Pageable pageable);

  long countByUserId(Long userId);

  Page<CommentEntity> findAllByCommentAreaAndAreaId(
      CommentArea commentArea, Long areaId, Pageable pageable);

  long countByCommentAreaAndAreaId(CommentArea commentArea, Long areaId);
}
