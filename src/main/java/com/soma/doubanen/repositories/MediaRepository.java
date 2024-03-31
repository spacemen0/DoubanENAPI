package com.soma.doubanen.repositories;

import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.enums.MediaType;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<MediaEntity, Long> {

  Page<MediaEntity> findByType(MediaType mediaType, Pageable pageable);

  List<MediaEntity> findByAuthorEntityId(Long id);

  long countByType(MediaType mediaType);
}
