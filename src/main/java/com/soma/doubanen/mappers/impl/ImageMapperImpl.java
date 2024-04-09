package com.soma.doubanen.mappers.impl;

import com.soma.doubanen.domains.dto.ImageDto;
import com.soma.doubanen.domains.entities.ImageEntity;
import com.soma.doubanen.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ImageMapperImpl implements Mapper<ImageEntity, ImageDto> {
  private final ModelMapper modelMapper;

  public ImageMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public ImageDto mapTo(ImageEntity imageEntity) {
    return modelMapper.map(imageEntity, ImageDto.class);
  }

  @Override
  public ImageEntity mapFrom(ImageDto imageDto) {
    return modelMapper.map(imageDto, ImageEntity.class);
  }
}
