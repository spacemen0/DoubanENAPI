package com.soma.doubanen.mappers.impl;

import com.soma.doubanen.domains.dto.CommentDto;
import com.soma.doubanen.domains.entities.CommentEntity;
import com.soma.doubanen.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CommentMapperImpl implements Mapper<CommentEntity, CommentDto> {
  private final ModelMapper modelMapper;

  public CommentMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public CommentDto mapTo(CommentEntity commentEntity) {
    return modelMapper.map(commentEntity, CommentDto.class);
  }

  @Override
  public CommentEntity mapFrom(CommentDto commentDto) {
    return modelMapper.map(commentDto, CommentEntity.class);
  }
}
