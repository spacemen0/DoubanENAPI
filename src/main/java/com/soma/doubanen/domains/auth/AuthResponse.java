package com.soma.doubanen.domains.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
  private String token;
  private Long userId;
  private String message;
}
