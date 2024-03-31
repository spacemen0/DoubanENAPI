package com.soma.doubanen.domains.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class SearchDto {

  private String text;

  private List<String> fields = new ArrayList<>();

  private int limit;
}
