package com.soma.doubanen.mappers.impl;

import com.soma.doubanen.domains.dto.UserDto;
import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements Mapper<UserEntity, UserDto> {

  private final ModelMapper modelMapper;

  public UserMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public UserDto mapTo(UserEntity userEntity) {
    userEntity.setPassword(null);
    userEntity.setEmail(null);
    return modelMapper.map(userEntity, UserDto.class);
  }

  @Override
  public UserEntity mapFrom(UserDto userDto) {
    return modelMapper.map(userDto, UserEntity.class);
  }
}
