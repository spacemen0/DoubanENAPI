package com.soma.doubanen.domains.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.soma.doubanen.domains.enums.ImageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageDto {
  private Long id;
  private Long objectId;
  private ImageType type;
  @JsonIgnore private byte[] imageData;
}
