package com.soma.doubanen.mappers.impl;

import com.soma.doubanen.domains.dto.MediaDto;
import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MediaMapperImpl implements Mapper<MediaEntity, MediaDto> {
  private final ModelMapper modelMapper;

  public MediaMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public MediaDto mapTo(MediaEntity mediaEntity) {
    return modelMapper.map(mediaEntity, MediaDto.class);
  }

  @Override
  public MediaEntity mapFrom(MediaDto mediaDto) {
    return modelMapper.map(mediaDto, MediaEntity.class);
  }
}
