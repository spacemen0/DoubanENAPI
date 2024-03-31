package com.soma.doubanen.domains.dto;

import com.soma.doubanen.domains.enums.MediaStatus;
import com.soma.doubanen.domains.enums.MediaType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaStatusDto {
  private Long id;

  private Float score;

  private LocalDate date;

  private MediaType type;

  private MediaStatus status;

  private Long mediaId;

  private Long userId;
}
