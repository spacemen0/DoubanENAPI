package com.soma.doubanen.services;

import com.soma.doubanen.domains.entities.ImageEntity;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.DataFormatException;

public interface ImageService {

  byte[] compressImage(byte[] data) throws IOException;

  byte[] decompressImage(byte[] data) throws DataFormatException, IOException;

  Optional<ImageEntity> findById(Long id);

  ImageEntity save(ImageEntity imageEntity);
}
