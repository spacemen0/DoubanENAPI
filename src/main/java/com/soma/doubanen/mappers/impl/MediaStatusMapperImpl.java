package com.soma.doubanen.mappers.impl;

import com.soma.doubanen.domains.dto.MediaStatusDto;
import com.soma.doubanen.domains.entities.MediaStatusEntity;
import com.soma.doubanen.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MediaStatusMapperImpl implements Mapper<MediaStatusEntity, MediaStatusDto> {
  private final ModelMapper modelMapper;

  public MediaStatusMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public MediaStatusDto mapTo(MediaStatusEntity userEntity) {
    return modelMapper.map(userEntity, MediaStatusDto.class);
  }

  @Override
  public MediaStatusEntity mapFrom(MediaStatusDto userDto) {
    return modelMapper.map(userDto, MediaStatusEntity.class);
  }
}
