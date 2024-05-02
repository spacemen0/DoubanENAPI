package com.soma.doubanen.mappers.impl;

import com.soma.doubanen.domains.dto.AuthorRequestDto;
import com.soma.doubanen.domains.entities.AuthorRequestEntity;
import com.soma.doubanen.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AuthorRequestMapperImpl implements Mapper<AuthorRequestEntity, AuthorRequestDto> {

  private final ModelMapper modelMapper;

  public AuthorRequestMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public AuthorRequestDto mapTo(AuthorRequestEntity authorRequestEntity) {
    return modelMapper.map(authorRequestEntity, AuthorRequestDto.class);
  }

  @Override
  public AuthorRequestEntity mapFrom(AuthorRequestDto authorRequestDto) {
    return modelMapper.map(authorRequestDto, AuthorRequestEntity.class);
  }
}
