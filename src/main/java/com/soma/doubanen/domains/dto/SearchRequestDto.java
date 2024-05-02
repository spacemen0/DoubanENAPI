package com.soma.doubanen.domains.dto;

import com.soma.doubanen.domains.enums.MediaType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class SearchRequestDto {

  @NotBlank private String text;

  private MediaType type;

  private List<String> fields = new ArrayList<>();

  @Min(1)
  private int limit;

  @Min(1)
  private int page;
}
