package com.soma.doubanen.domains.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.domains.enums.MediaType;
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
public class ReviewDto {
  private Long id;

  private Float score;

  private LocalDate date;

  private Long mediaId;

  private Long likes;

  @JsonIgnore private List<UserEntity> likedUsers;

  private MediaType type;

  private String title;

  private String content;

  private UserDto user;
}
