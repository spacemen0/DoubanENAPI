package com.soma.doubanen.domains.dto;

import com.soma.doubanen.domains.enums.AuthorType;
import com.soma.doubanen.domains.enums.MediaGenre;
import com.soma.doubanen.domains.enums.RequestStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorRequestDto {
  private Long id;

  private String name;

  private List<MediaGenre> genres;

  private AuthorType type;

  private Long userId;

  private Long resourceId;

  private RequestStatus status;

  private LocalDateTime actionTime;

  private String message;
}
