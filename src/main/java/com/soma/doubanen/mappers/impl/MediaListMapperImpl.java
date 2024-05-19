package com.soma.doubanen.mappers.impl;

import com.soma.doubanen.domains.dto.MediaListDto;
import com.soma.doubanen.domains.entities.MediaListEntity;
import com.soma.doubanen.domains.entities.UserEntity;
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
  public MediaListDto mapTo(MediaListEntity mediaList) {
    UserEntity user = mediaList.getUserEntity();
    user.setPassword(null);
    user.setEmail(null);
    mediaList.setUserEntity(user);
    return modelMapper.map(mediaList, MediaListDto.class);
  }

  @Override
  public MediaListEntity mapFrom(MediaListDto mediaList) {
    return modelMapper.map(mediaList, MediaListEntity.class);
  }
}
