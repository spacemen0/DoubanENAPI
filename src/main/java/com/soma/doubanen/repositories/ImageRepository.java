package com.soma.doubanen.repositories;

import com.soma.doubanen.domains.entities.ImageEntity;
import com.soma.doubanen.domains.enums.ImageType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

  void deleteAllByObjectIdAndType(Long objectId, ImageType type);

  Optional<ImageEntity> findByObjectIdAndType(Long objectId, ImageType type);
}
