package com.soma.doubanen.domains.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.soma.doubanen.domains.auth.Token;
import com.soma.doubanen.domains.enums.UserRole;
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
public class UserDto {

  private Long id;

  private String username;

  private String email;

  private UserRole role;

  private String password;

  private LocalDate date;

  private String profileImageUrl;

  private String bio;

  @JsonIgnore private List<Token> tokens;
}
