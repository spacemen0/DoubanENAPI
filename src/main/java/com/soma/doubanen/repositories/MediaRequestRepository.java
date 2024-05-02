package com.soma.doubanen.repositories;

import com.soma.doubanen.domains.entities.MediaRequestEntity;
import com.soma.doubanen.domains.enums.RequestStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRequestRepository extends JpaRepository<MediaRequestEntity, Long> {
  List<MediaRequestEntity> findByUserId(Long userId);

  List<MediaRequestEntity> findByStatus(RequestStatus status);
}
