package com.soma.doubanen.repositories;

import com.soma.doubanen.domains.entities.MediaListEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaListRepository extends JpaRepository<MediaListEntity, Long> {
  List<MediaListEntity> findByUserEntityId(Long userId);
}
