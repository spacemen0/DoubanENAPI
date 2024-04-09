package com.soma.doubanen.domains.dto;

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

  private byte[] imageData;
}
