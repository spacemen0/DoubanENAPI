package com.soma.doubanen.services.impl;

import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.entities.MediaStatusEntity;
import com.soma.doubanen.domains.enums.MediaStatus;
import com.soma.doubanen.domains.enums.MediaType;
import com.soma.doubanen.repositories.MediaRepository;
import com.soma.doubanen.repositories.MediaStatusRepository;
import com.soma.doubanen.repositories.UserRepository;
import com.soma.doubanen.services.MediaStatusService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MediaStatusServiceImpl implements MediaStatusService {

  private final MediaStatusRepository mediaStatusRepository;

  private final UserRepository userRepository;

  private final MediaRepository mediaRepository;

  public MediaStatusServiceImpl(
      MediaStatusRepository mediaStatusRepository,
      UserRepository userRepository,
      MediaRepository mediaRepository) {
    this.mediaStatusRepository = mediaStatusRepository;
    this.userRepository = userRepository;
    this.mediaRepository = mediaRepository;
  }

  @Override
  public Optional<MediaStatusEntity> save(MediaStatusEntity newStatus, Long id) {
    if ((!userRepository.existsById(newStatus.getUserId()))
        || (!mediaRepository.existsById(newStatus.getMediaId()))) {
      return Optional.empty();
    }

    Optional<MediaEntity> mediaEntity = mediaRepository.findById(newStatus.getMediaId());
    if (mediaEntity.isEmpty()) {
      return Optional.empty();
    }
    if (!mediaEntity.get().getType().equals(newStatus.getType())) return Optional.empty();
    MediaEntity media = mediaEntity.get();
    Float oldTotal = media.getAverage() * media.getRatings();
    if (newStatus.getStatus() == MediaStatus.Rated
        || newStatus.getStatus() == MediaStatus.Reviewed) {
      media.setAverage((oldTotal + newStatus.getScore()) / (media.getRatings() + 1));
      media.setRatings(media.getRatings() + 1);
    }
    if (newStatus.getStatus() == MediaStatus.Doing) media.setDoings(media.getDoings() + 1);
    if (newStatus.getStatus() == MediaStatus.Wishlist) media.setWants(media.getWants() + 1);

    Optional<MediaStatusEntity> existStatus =
        mediaStatusRepository.findOneByUserIdAndMediaId(
            newStatus.getUserId(), newStatus.getMediaId());

    if (existStatus.isPresent()) {
      MediaStatusEntity exist = existStatus.get();
      if (exist.getStatus() == MediaStatus.Wishlist) media.setWants(media.getWants() - 1);
      if (exist.getStatus() == MediaStatus.Doing) media.setDoings(media.getDoings() - 1);
      if (exist.getStatus() == MediaStatus.Rated || exist.getStatus() == MediaStatus.Reviewed) {
        if (media.getRatings() > 1) {
          Float newScore = newStatus.getScore() == null ? 0f : newStatus.getScore();
          media.setAverage((oldTotal + newScore - exist.getScore()) / (media.getRatings() - 1));
        } else {
          media.setAverage(0f);
        }
        media.setRatings(media.getRatings() - 1);
      }
      exist.setDate(LocalDate.now());
      exist.setScore(newStatus.getScore());
      exist.setStatus(newStatus.getStatus());
      mediaRepository.save(media);
      return Optional.of(mediaStatusRepository.save(exist));
    } else {
      newStatus.setId(id);
      newStatus.setDate(LocalDate.now());
      mediaRepository.save(media);
      return Optional.of(mediaStatusRepository.save(newStatus));
    }
  }

  @Override
  public Optional<MediaStatusEntity> findOne(Long id) {
    return mediaStatusRepository.findById(id);
  }

  @Override
  public List<MediaStatusEntity> findAll() {
    return mediaStatusRepository.findAll();
  }

  @Override
  public Page<MediaEntity> findAllWithPagination(
      Pageable pageable, Long userId, MediaStatus status) {
    Page<MediaStatusEntity> mediaStatusPage =
        mediaStatusRepository.findAllByUserIdAndStatus(pageable, userId, status);
    return mediaStatusPage.map(this::getMediaEntityFromMediaStatusEntity);
  }

  @Override
  public Page<MediaEntity> findByTypeWithPagination(
      Pageable pageable, MediaType type, Long userId, MediaStatus status) {
    Page<MediaStatusEntity> mediaStatusPage =
        mediaStatusRepository.findAllByUserIdAndTypeAndStatus(pageable, userId, type, status);
    return mediaStatusPage.map(this::getMediaEntityFromMediaStatusEntity);
  }

  private MediaEntity getMediaEntityFromMediaStatusEntity(MediaStatusEntity mediaStatusEntity) {
    // Fetch MediaEntity from MediaRepository using the mediaId in MediaStatusEntity
    Optional<MediaEntity> mediaEntityOptional =
        mediaRepository.findById(mediaStatusEntity.getMediaId());
    return mediaEntityOptional.orElse(null);
  }

  @Override
  public Long countAllByUserIdAndStatus(Long userId, MediaStatus status) {
    return mediaStatusRepository.countByUserIdAndStatus(userId, status);
  }

  @Override
  public Long countByTypeAndUserIdAndStatus(MediaType type, Long userId, MediaStatus status) {
    return mediaStatusRepository.countByTypeAndUserIdAndStatus(type, userId, status);
  }

  @Override
  public List<MediaStatusEntity> findByTypeAndUserIdAndStatus(
      MediaType type, Long userId, MediaStatus status) {
    return mediaStatusRepository.findByTypeAndUserIdAndStatus(type, userId, status);
  }

  public Optional<MediaStatusEntity> findByUserIdAndMediaId(Long userId, Long mediaId) {
    return mediaStatusRepository.findOneByUserIdAndMediaId(userId, mediaId);
  }

  @Override
  public List<MediaStatusEntity> getUserCurrentOn(Long userId) {
    Optional<MediaStatusEntity> music =
        mediaStatusRepository.findUserLatestByType(MediaType.Music, userId);
    Optional<MediaStatusEntity> movie =
        mediaStatusRepository.findUserLatestByType(MediaType.Movie, userId);
    Optional<MediaStatusEntity> book =
        mediaStatusRepository.findUserLatestByType(MediaType.Book, userId);
    List<MediaStatusEntity> result = new ArrayList<>();
    Stream.of(music, movie, book)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(result::add);
    return result;
  }

  @Override
  public boolean notExists(Long id) {
    return !mediaStatusRepository.existsById(id);
  }

  @Override
  public void delete(Long id) {
    Optional<MediaStatusEntity> existStatus = mediaStatusRepository.findById(id);
    if (existStatus.isPresent()) {
      MediaStatusEntity exist = existStatus.get();
      Optional<MediaEntity> ifMediaEntity = mediaRepository.findById(exist.getMediaId());
      if (ifMediaEntity.isEmpty()) {
        return;
      }
      MediaEntity existMedia = ifMediaEntity.get();
      if (exist.getStatus() == MediaStatus.Wishlist) existMedia.setWants(existMedia.getWants() - 1);
      if (exist.getStatus() == MediaStatus.Reviewed || exist.getStatus() == MediaStatus.Rated) {
        if (existMedia.getRatings() > 1) {
          float newAverage =
              (existMedia.getAverage() * existMedia.getRatings() - exist.getScore())
                  / (existMedia.getRatings() - 1);
          existMedia.setAverage(newAverage);
        } else {
          existMedia.setAverage(0.0f);
        }
        existMedia.setRatings(existMedia.getRatings() - 1);
      }
      if (exist.getStatus() == MediaStatus.Doing) existMedia.setDoings(existMedia.getDoings() - 1);
      mediaRepository.save(existMedia);
    }
    mediaStatusRepository.deleteById(id);
  }

  @Override
  public MediaStatusEntity partialUpdate(Long id, MediaStatusEntity mediaStatusEntity) {
    mediaStatusEntity.setId(id);
    return mediaStatusRepository
        .findById(id)
        .map(
            existingMediaStatus -> {
              Optional.ofNullable(mediaStatusEntity.getScore())
                  .ifPresent(existingMediaStatus::setScore);
              Optional.ofNullable(mediaStatusEntity.getDate())
                  .ifPresent(existingMediaStatus::setDate);
              Optional.ofNullable(mediaStatusEntity.getStatus())
                  .ifPresent(existingMediaStatus::setStatus);
              Optional.ofNullable(mediaStatusEntity.getMediaId())
                  .ifPresent(existingMediaStatus::setMediaId);
              Optional.ofNullable(mediaStatusEntity.getUserId())
                  .ifPresent(existingMediaStatus::setUserId);
              return existingMediaStatus;
            })
        .orElseThrow(() -> new RuntimeException("Media status not found"));
  }
}
