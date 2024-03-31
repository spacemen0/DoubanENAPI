package com.soma.doubanen.domains.dto;

import com.fasterxml.jackson.annotation.*;
import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.entities.UserEntity;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaListDto {
  private Long id;

  private String title;

  private String description;

  private LocalDate date;

  @JsonIgnore private List<MediaEntity> medias;

  private UserEntity user;
}
