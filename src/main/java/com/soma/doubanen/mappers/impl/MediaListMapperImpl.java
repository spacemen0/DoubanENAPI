package com.soma.doubanen.mappers.impl;

import com.soma.doubanen.domains.dto.MediaListDto;
import com.soma.doubanen.domains.entities.MediaListEntity;
import com.soma.doubanen.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MediaListMapperImpl implements Mapper<MediaListEntity, MediaListDto> {
  private final ModelMapper modelMapper;

  public MediaListMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public MediaListDto mapTo(MediaListEntity userEntity) {
    return modelMapper.map(userEntity, MediaListDto.class);
  }

  @Override
  public MediaListEntity mapFrom(MediaListDto userDto) {
    return modelMapper.map(userDto, MediaListEntity.class);
  }
}
