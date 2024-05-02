package com.soma.doubanen.mappers.impl;

import com.soma.doubanen.domains.dto.MediaRequestDto;
import com.soma.doubanen.domains.entities.MediaRequestEntity;
import com.soma.doubanen.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MediaRequestMapperImpl implements Mapper<MediaRequestEntity, MediaRequestDto> {
    private final ModelMapper modelMapper;


    public MediaRequestMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public MediaRequestDto mapTo(MediaRequestEntity mediaEntity) {
        return modelMapper.map(mediaEntity, MediaRequestDto.class);
    }

    @Override
    public MediaRequestEntity mapFrom(MediaRequestDto mediaDto) {
        return modelMapper.map(mediaDto, MediaRequestEntity.class);
    }
}
