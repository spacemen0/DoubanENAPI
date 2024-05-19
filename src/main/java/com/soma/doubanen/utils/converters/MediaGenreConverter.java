package com.soma.doubanen.utils.converters;

import com.soma.doubanen.domains.enums.MediaGenre;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class MediaGenreConverter implements Converter<String, MediaGenre> {

  @Override
  public MediaGenre convert(@NonNull String source) {
    for (MediaGenre genre : MediaGenre.values()) {
      if (genre.getGenre().equalsIgnoreCase(source)) {
        return genre;
      }
    }
    throw new IllegalArgumentException("Invalid MediaGenre: " + source);
  }
}
