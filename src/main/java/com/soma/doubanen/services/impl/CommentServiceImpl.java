package com.soma.doubanen.services.impl;

import com.soma.doubanen.domains.entities.CommentEntity;
import com.soma.doubanen.domains.enums.CommentArea;
import com.soma.doubanen.repositories.*;
import com.soma.doubanen.services.CommentService;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;

  private final MediaRepository mediaRepository;

  private final MediaListRepository mediaListRepository;

  private final ReviewRepository reviewRepository;

  private final UserRepository userRepository;

  public CommentServiceImpl(
      CommentRepository commentRepository,
      MediaRepository mediaRepository,
      MediaListRepository mediaListRepository,
      ReviewRepository reviewRepository,
      UserRepository userRepository) {
    this.commentRepository = commentRepository;
    this.mediaRepository = mediaRepository;
    this.mediaListRepository = mediaListRepository;
    this.reviewRepository = reviewRepository;
    this.userRepository = userRepository;
  }

  @Override
  public CommentEntity save(Long id, CommentEntity commentEntity) {
    if (id == null) {
      if (!userRepository.existsById(commentEntity.getUserId())) {
        return null;
      }
      switch (commentEntity.getCommentArea()) {
        case MediaList -> {
          if (!mediaListRepository.existsById(commentEntity.getAreaId())) {
            return null;
          }
        }
        case Review -> {
          if (!reviewRepository.existsById(commentEntity.getAreaId())) {
            return null;
          }
        }
        case Media -> {
          if (!mediaRepository.existsById(commentEntity.getAreaId())) {
            return null;
          }
        }
        case User -> {
          if (!userRepository.existsById(commentEntity.getAreaId())) {
            return null;
          }
        }
      }
      commentEntity.setDate(LocalDate.now());
      return commentRepository.save(commentEntity);
    } else {
      Optional<CommentEntity> optionalCommentEntity = commentRepository.findById(id);
      if (optionalCommentEntity.isEmpty()) {
        return null;
      }
      CommentEntity comment = optionalCommentEntity.get();
      comment.setDate(LocalDate.now());
      comment.setContent(commentEntity.getContent());
      return commentRepository.save(comment);
    }
  }

  @Override
  public Optional<CommentEntity> findById(Long id) {
    return commentRepository.findById(id);
  }

  @Override
  public Page<CommentEntity> findAllByCommentAreaAndAreaId(
      Long id, CommentArea area, Pageable pageable) {
    return commentRepository.findAllByCommentAreaAndAreaId(area, id, pageable);
  }

  @Override
  public Page<CommentEntity> findAllByUserId(Long userId, Pageable pageable) {
    return commentRepository.findAllByUserId(userId, pageable);
  }

  @Override
  public Long countByUserId(Long userId) {
    return commentRepository.countByUserId(userId);
  }

  @Override
  public Long countByCommentAreaAndAreaId(CommentArea area, Long areaId) {
    return commentRepository.countByCommentAreaAndAreaId(area, areaId);
  }

  @Override
  public void delete(Long id) {
    commentRepository.deleteById(id);
  }
}
