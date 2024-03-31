package com.soma.doubanen.domains.dto;

import com.soma.doubanen.domains.enums.AuthorType;
import com.soma.doubanen.domains.enums.MediaGenre;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorDto {
  private Long id;

  private String name;

  private List<MediaGenre> genres;

  private AuthorType type;

  private List<MediaDto> mediaDtos;
}
