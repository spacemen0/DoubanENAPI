package com.soma.doubanen.domains.dto;

import com.soma.doubanen.domains.entities.UserEntity;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {
  private Long id;

  private Float score;

  private LocalDate date;

  private Long mediaId;

  private String title;

  private String content;

  private UserEntity user;
}
