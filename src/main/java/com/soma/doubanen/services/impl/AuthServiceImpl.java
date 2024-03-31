package com.soma.doubanen.services.impl;

import com.soma.doubanen.domains.auth.AuthResponse;
import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.repositories.TokenRepository;
import com.soma.doubanen.repositories.UserRepository;
import com.soma.doubanen.services.AuthService;
import com.soma.doubanen.services.TokenService;
import java.time.LocalDate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;

  private final TokenRepository tokenRepository;

  private final AuthenticationManager authenticationManager;

  public AuthServiceImpl(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      TokenService tokenService,
      TokenRepository tokenRepository,
      AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.tokenService = tokenService;
    this.tokenRepository = tokenRepository;
    this.authenticationManager = authenticationManager;
  }

  @Override
  public AuthResponse register(UserEntity request) {
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      return new AuthResponse(null, null, "User already exist");
    }
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      return new AuthResponse(null, null, "User already exist");
    }

    UserEntity user =
        UserEntity.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .role(request.getRole())
            .password(passwordEncoder.encode(request.getPassword()))
            .date(LocalDate.now())
            .build();

    UserEntity savedUser = userRepository.save(user);

    String jwt = tokenService.generateToken(savedUser);

    saveUserToken(jwt, savedUser, tokenRepository);

    return new AuthResponse(jwt, savedUser.getId(), "User registration was successful");
  }

  @Override
  public AuthResponse authenticate(UserEntity request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    UserEntity user = userRepository.findByUsername(request.getUsername()).orElseThrow();
    String jwt = tokenService.generateToken(user);

    revokeAllTokenByUser(user, tokenRepository);
    saveUserToken(jwt, user, tokenRepository);

    return new AuthResponse(jwt, user.getId(), "User login was successful");
  }
}
