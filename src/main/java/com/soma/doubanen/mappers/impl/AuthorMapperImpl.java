package com.soma.doubanen.mappers.impl;

import com.soma.doubanen.domains.dto.AuthorDto;
import com.soma.doubanen.domains.entities.AuthorEntity;
import com.soma.doubanen.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapperImpl implements Mapper<AuthorEntity, AuthorDto> {
  private final ModelMapper modelMapper;

  public AuthorMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public AuthorDto mapTo(AuthorEntity authorEntity) {
    return modelMapper.map(authorEntity, AuthorDto.class);
  }

  @Override
  public AuthorEntity mapFrom(AuthorDto authorDto) {
    return modelMapper.map(authorDto, AuthorEntity.class);
  }
}
