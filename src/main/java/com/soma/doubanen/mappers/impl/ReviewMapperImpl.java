package com.soma.doubanen.mappers.impl;

import com.soma.doubanen.domains.dto.ReviewDto;
import com.soma.doubanen.domains.entities.ReviewEntity;
import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapperImpl implements Mapper<ReviewEntity, ReviewDto> {
  private final ModelMapper modelMapper;

  public ReviewMapperImpl(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  @Override
  public ReviewDto mapTo(ReviewEntity reviewEntity) {
    UserEntity user = reviewEntity.getUser();
    user.setPassword(null);
    reviewEntity.setUser(user);
    return modelMapper.map(reviewEntity, ReviewDto.class);
  }

  @Override
  public ReviewEntity mapFrom(ReviewDto userDto) {
    return modelMapper.map(userDto, ReviewEntity.class);
  }
}
