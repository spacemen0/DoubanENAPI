package com.soma.doubanen.repositories;

import com.soma.doubanen.domains.entities.AuthorRequestEntity;
import com.soma.doubanen.domains.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRequestRepository extends JpaRepository<AuthorRequestEntity, Long> {
  List<AuthorRequestEntity> findByUserId(Long userId);

  List<AuthorRequestEntity> findByStatus(RequestStatus status);
}
