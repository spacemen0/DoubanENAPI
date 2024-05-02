package com.soma.doubanen.services.impl;

import com.soma.doubanen.domains.entities.AuthorEntity;
import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.entities.MediaRequestEntity;
import com.soma.doubanen.domains.enums.MediaGenre;
import com.soma.doubanen.domains.enums.MediaType;
import com.soma.doubanen.domains.enums.RequestStatus;
import com.soma.doubanen.repositories.MediaRequestRepository;
import com.soma.doubanen.services.MediaRequestService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MediaRequestServiceImpl implements MediaRequestService {

  private final MediaRequestRepository mediaRequestRepository;

  public MediaRequestServiceImpl(MediaRequestRepository mediaRequestRepository) {
    this.mediaRequestRepository = mediaRequestRepository;
  }

  @Override
  public MediaRequestEntity toMediaRequest(
      MediaEntity mediaEntity, RequestStatus status, Long userId) {

    MediaRequestEntity mediaRequestEntity = new MediaRequestEntity();

    copyFields(
        mediaRequestEntity,
        mediaEntity.getTitle(),
        mediaEntity.getDescription(),
        mediaEntity.getAdditionalInfo(),
        mediaEntity.getReleaseDate(),
        mediaEntity.getAverage(),
        mediaEntity.getRatings(),
        mediaEntity.getWants(),
        mediaEntity.getDoings(),
        mediaEntity.getGenre(),
        mediaEntity.getType(),
        mediaEntity.getImageUrl(),
        mediaEntity.getAuthor_name(),
        mediaEntity.getAuthorEntity(),
        null);

    mediaRequestEntity.setActionTime(LocalDateTime.now());
    mediaRequestEntity.setStatus(status);
    mediaRequestEntity.setUserId(userId);

    return mediaRequestEntity;
  }

  @Override
  public MediaEntity toMedia(MediaRequestEntity mediaRequestEntity) {
    MediaEntity mediaEntity = new MediaEntity();

    copyFields(
        null,
        mediaRequestEntity.getTitle(),
        mediaRequestEntity.getDescription(),
        mediaRequestEntity.getAdditionalInfo(),
        mediaRequestEntity.getReleaseDate(),
        mediaRequestEntity.getAverage(),
        mediaRequestEntity.getRatings(),
        mediaRequestEntity.getWants(),
        mediaRequestEntity.getDoings(),
        mediaRequestEntity.getGenre(),
        mediaRequestEntity.getType(),
        mediaRequestEntity.getImageUrl(),
        mediaRequestEntity.getAuthor_name(),
        mediaRequestEntity.getAuthorEntity(),
        mediaEntity);

    return mediaEntity;
  }

  private void copyFields(
      MediaRequestEntity mediaRequestEntity,
      String title,
      String description,
      String additionalInfo,
      LocalDate releaseDate,
      Float average,
      Long ratings,
      Long wants,
      Long doings,
      MediaGenre genre,
      MediaType type,
      String imageUrl,
      String authorName,
      AuthorEntity authorEntity,
      MediaEntity mediaEntity) {
    if (mediaRequestEntity != null) {
      mediaRequestEntity.setTitle(title);
      mediaRequestEntity.setDescription(description);
      mediaRequestEntity.setAdditionalInfo(additionalInfo);
      mediaRequestEntity.setReleaseDate(releaseDate);
      mediaRequestEntity.setAverage(average);
      mediaRequestEntity.setRatings(ratings);
      mediaRequestEntity.setWants(wants);
      mediaRequestEntity.setDoings(doings);
      mediaRequestEntity.setGenre(genre);
      mediaRequestEntity.setType(type);
      mediaRequestEntity.setImageUrl(imageUrl);
      mediaRequestEntity.setAuthor_name(authorName);
      mediaRequestEntity.setAuthorEntity(authorEntity);
    }
    if (mediaEntity != null) {
      mediaEntity.setTitle(title);
      mediaEntity.setDescription(description);
      mediaEntity.setAdditionalInfo(additionalInfo);
      mediaEntity.setReleaseDate(releaseDate);
      mediaEntity.setAverage(average);
      mediaEntity.setRatings(ratings);
      mediaEntity.setWants(wants);
      mediaEntity.setDoings(doings);
      mediaEntity.setGenre(genre);
      mediaEntity.setType(type);
      mediaEntity.setImageUrl(imageUrl);
      mediaEntity.setAuthor_name(authorName);
      mediaEntity.setAuthorEntity(authorEntity);
    }
  }

  @Override
  public MediaRequestEntity save(MediaRequestEntity mediaRequestEntity) {
    return mediaRequestRepository.save(mediaRequestEntity);
  }

  @Override
  public Optional<MediaRequestEntity> findById(Long id) {
    return mediaRequestRepository.findById(id);
  }

  @Override
  public void delete(Long id) {
    mediaRequestRepository.deleteById(id);
  }

  @Override
  public List<MediaRequestEntity> findByUserId(Long userId) {
    return mediaRequestRepository.findByUserId(userId);
  }

  @Override
  public List<MediaRequestEntity> findByStatus(RequestStatus status) {
    return mediaRequestRepository.findByStatus(status);
  }
}
