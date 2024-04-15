package com.soma.doubanen.services.impl;

import com.soma.doubanen.domains.entities.ReviewEntity;
import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.domains.enums.MediaType;
import com.soma.doubanen.repositories.ReviewRepository;
import com.soma.doubanen.repositories.UserRepository;
import com.soma.doubanen.services.ReviewService;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {
  private final ReviewRepository reviewRepository;
  private final UserRepository userRepository;

  public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository) {
    this.reviewRepository = reviewRepository;
    this.userRepository = userRepository;
  }

  @Override
  public ReviewEntity save(ReviewEntity reviewEntity, Long id) throws Exception {
    reviewEntity.setId(id);
    Optional<UserEntity> userEntity = userRepository.findById(reviewEntity.getUser().getId());
    if (userEntity.isEmpty()) throw new Exception("Runtime Error");
    reviewEntity.setDate(LocalDate.now());
    reviewEntity.setUser(userEntity.get());
    return reviewRepository.save(reviewEntity);
  }

  @Override
  public Optional<ReviewEntity> findOne(Long id) {
    return reviewRepository.findById(id);
  }

  @Override
  public Page<ReviewEntity> findAllByMediaId(Long mediaId, Pageable pageable) {
    return reviewRepository.findByMediaId(mediaId, pageable);
  }

  @Override
  public Page<ReviewEntity> findAllByUserIdAndType(Long userId, MediaType type, Pageable pageable) {
    return reviewRepository.findByUserIdAndType(userId, type, pageable);
  }

  @Override
  public void deleteByUserIdAndMediaId(Long userId, Long mediaId) {
    reviewRepository.deleteByUserIdAndMediaId(userId, mediaId);
  }

  public Long countAllByMediaId(Long mediaId) {
    return reviewRepository.countByMediaId(mediaId);
  }

  @Override
  public Long countAllByUserIdAndMediaType(Long userId, MediaType type) {
    return reviewRepository.countByUserIdAndType(userId, type);
  }

  @Override
  public boolean notExists(Long id) {
    return !reviewRepository.existsById(id);
  }

  @Override
  public void delete(Long id) {
    reviewRepository.deleteById(id);
  }

  @Override
  public ReviewEntity partialUpdate(Long id, ReviewEntity reviewEntity) {
    reviewEntity.setId(id);
    return reviewRepository
        .findById(id)
        .map(
            existingReviewEntity -> {
              if (reviewEntity.getScore() != null)
                existingReviewEntity.setScore(reviewEntity.getScore());
              if (reviewEntity.getDate() != null)
                existingReviewEntity.setDate(reviewEntity.getDate());
              if (reviewEntity.getMediaId() != null)
                existingReviewEntity.setMediaId(reviewEntity.getMediaId());
              if (reviewEntity.getTitle() != null)
                existingReviewEntity.setTitle(reviewEntity.getTitle());
              if (reviewEntity.getContent() != null)
                existingReviewEntity.setContent(reviewEntity.getContent());
              if (reviewEntity.getUser() != null)
                existingReviewEntity.setUser(reviewEntity.getUser());
              return reviewRepository.save(existingReviewEntity);
            })
        .orElseThrow(() -> new RuntimeException("Review not found"));
  }
}
