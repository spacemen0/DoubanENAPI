package com.soma.doubanen.services.impl;

import com.soma.doubanen.domains.entities.ReviewEntity;
import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.domains.enums.MediaType;
import com.soma.doubanen.repositories.ReviewRepository;
import com.soma.doubanen.services.ReviewService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewServiceImpl implements ReviewService {
  private final ReviewRepository reviewRepository;

  @PersistenceContext private EntityManager entityManager;

  public ReviewServiceImpl(ReviewRepository reviewRepository) {
    this.reviewRepository = reviewRepository;
  }

  @Override
  @Transactional
  public ReviewEntity save(ReviewEntity reviewEntity, Long id) throws Exception {
    reviewEntity.setId(id);
    UserEntity userEntity = entityManager.find(UserEntity.class, reviewEntity.getUser().getId());
    if (userEntity == null) throw new Exception("Runtime Error");
    reviewEntity.setDate(LocalDate.now());
    reviewEntity.setLikes(0L);
    userEntity = entityManager.merge(userEntity);
    reviewEntity.setUser(userEntity);
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
  public void like(ReviewEntity reviewEntity, UserEntity user) {
    List<UserEntity> users = reviewEntity.getLikedUsers();
    long userIdToAdd = user.getId();

    for (UserEntity likedUser : users) {
      if (likedUser.getId() == userIdToAdd) {
        return;
      }
    }
    users.add(user);
    reviewEntity.setLikes(reviewEntity.getLikes() + 1);
    reviewEntity.setLikedUsers(users);
    reviewRepository.save(reviewEntity);
  }

  @Override
  public void unlike(ReviewEntity reviewEntity, UserEntity user) {
    List<UserEntity> users = reviewEntity.getLikedUsers();
    long userIdToRemove = user.getId();

    Iterator<UserEntity> iterator = users.iterator();
    while (iterator.hasNext()) {
      UserEntity likedUser = iterator.next();
      if (likedUser.getId() == userIdToRemove) {
        if (reviewEntity.getLikes() > 0) {
          reviewEntity.setLikes(reviewEntity.getLikes() - 1);
        }
        iterator.remove();
        break;
      }
    }
    reviewEntity.setLikedUsers(users);
    reviewRepository.save(reviewEntity);
  }

  @Override
  public boolean isLiked(ReviewEntity review, UserEntity user) {
    List<UserEntity> users = review.getLikedUsers();
    long userIdToCompare = user.getId();

    for (UserEntity likedUser : users) {
      if (likedUser.getId() == userIdToCompare) {
        return true;
      }
    }
    return false;
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
