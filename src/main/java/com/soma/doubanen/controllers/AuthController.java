package com.soma.doubanen.controllers;

import com.soma.doubanen.domains.auth.AuthResponse;
import com.soma.doubanen.domains.dto.UserDto;
import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.domains.enums.UserRole;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;

  private final Mapper<UserEntity, UserDto> userMapper;

  public AuthController(AuthService authService, Mapper<UserEntity, UserDto> userMapper) {
    this.authService = authService;
    this.userMapper = userMapper;
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@RequestBody UserDto request) {
    request.setRole(UserRole.Standard);
    return ResponseEntity.ok(authService.register(userMapper.mapFrom(request)));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody UserDto request) {
    AuthResponse authResponse;
    try {
      authResponse = authService.authenticate(userMapper.mapFrom(request));
      return ResponseEntity.ok(authResponse);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }
}
