package com.soma.doubanen.services.impl;

import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.entities.MediaListEntity;
import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.repositories.MediaListRepository;
import com.soma.doubanen.repositories.MediaRepository;
import com.soma.doubanen.repositories.UserRepository;
import com.soma.doubanen.services.MediaListService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MediaListServiceImpl implements MediaListService {

  private final MediaListRepository mediaListRepository;
  private final MediaRepository mediaRepository;

  private final UserRepository userRepository;

  public MediaListServiceImpl(
      MediaListRepository mediaListRepository,
      MediaRepository mediaRepository,
      UserRepository userRepository) {
    this.mediaListRepository = mediaListRepository;
    this.mediaRepository = mediaRepository;
    this.userRepository = userRepository;
  }

  @Override
  public Optional<MediaListEntity> save(MediaListEntity mediaListEntity, Long id) throws Exception {
    Optional<UserEntity> userEntity =
        userRepository.findById(mediaListEntity.getUserEntity().getId());
    if (userEntity.isEmpty()) throw new Exception("Runtime Error");
    mediaListEntity.setId(id);
    mediaListEntity.setDate(LocalDate.now());
    mediaListEntity.setUserEntity(userEntity.get());
    return Optional.of(mediaListRepository.save(mediaListEntity));
  }

  @Override
  public Optional<MediaListEntity> findOne(Long id) {
    return mediaListRepository.findById(id);
  }

  @Override
  public List<MediaListEntity> findAll() {
    return mediaListRepository.findAll();
  }

  public Long countById(Long id) {
    Optional<MediaListEntity> mediaListEntity = mediaListRepository.findById(id);
    return mediaListEntity
        .map(listEntity -> (long) listEntity.getMediaEntities().size())
        .orElse(0L);
  }

  @Override
  public boolean notExists(Long id) {
    return !mediaListRepository.existsById(id);
  }

  @Override
  public void delete(Long id) {
    mediaListRepository.deleteById(id);
  }

  @Override
  public MediaListEntity partialUpdate(Long id, MediaListEntity mediaListEntity) {
    mediaListEntity.setId(id);
    return mediaListRepository
        .findById(id)
        .map(
            existingMediaList -> {
              Optional.ofNullable(mediaListEntity.getTitle())
                  .ifPresent(existingMediaList::setTitle);
              Optional.ofNullable(mediaListEntity.getDescription())
                  .ifPresent(existingMediaList::setDescription);
              Optional.ofNullable(mediaListEntity.getDate()).ifPresent(existingMediaList::setDate);
              Optional.ofNullable(mediaListEntity.getMediaEntities())
                  .ifPresent(existingMediaList::setMediaEntities);
              Optional.ofNullable(mediaListEntity.getUserEntity())
                  .ifPresent(existingMediaList::setUserEntity);
              return existingMediaList;
            })
        .orElseThrow(() -> new RuntimeException("Media list not found"));
  }

  @Override
  public List<MediaListEntity> findByUserId(Long userId) {
    return mediaListRepository.findByUserEntityId(userId);
  }

  @Override
  public Boolean addMediaToList(Long id, Long mediaId) {
    Optional<MediaListEntity> mediaList = mediaListRepository.findById(id);
    if (mediaList.isPresent()) {
      MediaListEntity newMediaList = mediaList.get();
      List<MediaEntity> newMediaEntities = newMediaList.getMediaEntities();
      Optional<MediaEntity> media = mediaRepository.findById(mediaId);
      if (media.isEmpty()) return false;
      newMediaEntities.add(media.get());
      mediaListRepository.save(newMediaList);
      return true;
    } else return false;
  }
}
