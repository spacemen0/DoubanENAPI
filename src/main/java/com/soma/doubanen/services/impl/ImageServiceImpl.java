package com.soma.doubanen.services.impl;

import com.soma.doubanen.domains.entities.ImageEntity;
import com.soma.doubanen.domains.enums.ImageType;
import com.soma.doubanen.repositories.ImageRepository;
import com.soma.doubanen.services.ImageService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.springframework.stereotype.Service;

@Service
public class ImageServiceImpl implements ImageService {

  private static final int BITE_SIZE = 4 * 1024;

  private final ImageRepository imageRepository;

  public ImageServiceImpl(ImageRepository imageRepository) {
    this.imageRepository = imageRepository;
  }

  public byte[] compressImage(byte[] data) throws IOException {
    Deflater deflater = new Deflater();
    deflater.setLevel(Deflater.BEST_COMPRESSION);
    deflater.setInput(data);
    deflater.finish();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
    byte[] tmp = new byte[BITE_SIZE];

    while (!deflater.finished()) {
      int size = deflater.deflate(tmp);
      outputStream.write(tmp, 0, size);
    }

    outputStream.close();
    return outputStream.toByteArray();
  }

  public byte[] decompressImage(byte[] data) throws DataFormatException, IOException {
    Inflater inflater = new Inflater();
    inflater.setInput(data);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
    byte[] tmp = new byte[BITE_SIZE];

    while (!inflater.finished()) {
      int count = inflater.inflate(tmp);
      outputStream.write(tmp, 0, count);
    }

    outputStream.close();
    return outputStream.toByteArray();
  }

  @Override
  public Optional<ImageEntity> findById(Long id) {
    return imageRepository.findById(id);
  }

  @Override
  public ImageEntity save(ImageEntity imageEntity) {
    Long objectId = imageEntity.getObjectId();
    ImageType type = imageEntity.getType();
    if (objectId != null && type != null) {
      Optional<ImageEntity> image = imageRepository.findByObjectIdAndType(objectId, type);
      if (image.isEmpty()) {
        return imageRepository.save(imageEntity);
      } else {
        ImageEntity savedImage = image.get();
        savedImage.setImageData(imageEntity.getImageData());
        return imageRepository.save(savedImage);
      }
    }
    return null;
  }
}
