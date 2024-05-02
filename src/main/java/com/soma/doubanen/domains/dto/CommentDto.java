package com.soma.doubanen.domains.dto;

import com.soma.doubanen.domains.enums.CommentArea;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
  private Long id;

  private LocalDate date;

  private CommentArea commentArea;

  private String content;

  private Long areaId;

  private Long userId;
}
