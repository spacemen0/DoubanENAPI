package com.soma.doubanen.services;

import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.enums.MediaType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MediaService {

  Optional<MediaEntity> save(MediaEntity mediaEntity, Long id);

  boolean check(MediaEntity mediaEntity, Long id);

  Optional<MediaEntity> findOne(Long id);

  List<MediaEntity> findAll();

  Page<MediaEntity> findAll(Pageable pageable);

  List<MediaEntity> searchMedias(
      String text, List<String> fields, int page, int limit, MediaType type);

  Page<MediaEntity> findAll(Pageable pageable, MediaType mediaType);

  Long countAll();

  Long countByType(MediaType mediaType);

  boolean notExists(Long id);

  void delete(Long id);

  MediaEntity partialUpdate(Long id, MediaEntity mediaEntity);
}
