package com.soma.doubanen.services;

import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.entities.MediaRequestEntity;
import com.soma.doubanen.domains.enums.RequestStatus;
import java.util.List;
import java.util.Optional;

public interface MediaRequestService {

  MediaRequestEntity toMediaRequest(MediaEntity mediaEntity, RequestStatus status, Long userId);

  MediaEntity toMedia(MediaRequestEntity mediaEntity);

  MediaRequestEntity save(MediaRequestEntity mediaRequestEntity);

  Optional<MediaRequestEntity> findById(Long id);

  void delete(Long id);

  List<MediaRequestEntity> findByUserId(Long userId);

  List<MediaRequestEntity> findByStatus(RequestStatus status);
}
