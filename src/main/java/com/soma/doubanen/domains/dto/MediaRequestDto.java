package com.soma.doubanen.domains.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.soma.doubanen.domains.enums.MediaGenre;
import com.soma.doubanen.domains.enums.MediaType;
import com.soma.doubanen.domains.enums.RequestStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaRequestDto {

  private Long id;

  private String title;

  private String description;

  private String additional;

  private LocalDate releaseDate;

  private Float average;

  private Long ratings;

  private MediaType type;

  private Long wants;

  private Long doings;

  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonProperty("author")
  @JsonIdentityReference(alwaysAsId = true)
  private AuthorDto authorDto;

  private String author_name;

  private MediaGenre genre;

  private String imageUrl;

  private Long userId;

  private Long resourceId;

  private RequestStatus status;

  private LocalDateTime actionTime;

  private String message;
}
