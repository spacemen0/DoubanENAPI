package com.soma.doubanen.services;

import com.soma.doubanen.domains.entities.MediaListEntity;
import java.util.List;
import java.util.Optional;

public interface MediaListService {
  Optional<MediaListEntity> save(MediaListEntity mediaListEntity, Long id) throws Exception;

  Optional<MediaListEntity> findOne(Long id);

  List<MediaListEntity> findAll();

  Long countById(Long id);

  boolean notExists(Long id);

  void delete(Long id);

  MediaListEntity partialUpdate(Long id, MediaListEntity mediaListEntity);

  List<MediaListEntity> findByUserId(Long userId);

  Boolean addMediaToList(Long id, Long mediaEntity);
}
