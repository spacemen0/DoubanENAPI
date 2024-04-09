package com.soma.doubanen.repositories;

import com.soma.doubanen.domains.entities.ImageEntity;
import com.soma.doubanen.domains.enums.ImageType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

  boolean existsByObjectIdAndType(Long objectId, ImageType imageType);
  Optional<ImageEntity>findByObjectIdAndType(Long objectId,ImageType type);
}
