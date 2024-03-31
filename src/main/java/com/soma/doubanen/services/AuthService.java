package com.soma.doubanen.services;

import com.soma.doubanen.domains.auth.AuthResponse;
import com.soma.doubanen.domains.auth.Token;
import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.repositories.TokenRepository;
import java.util.List;

public interface AuthService {

  AuthResponse register(UserEntity request);

  AuthResponse authenticate(UserEntity request);

  default void revokeAllTokenByUser(UserEntity user, TokenRepository tokenRepository) {
    List<Token> validTokens = tokenRepository.findAllTokensByUser(user.getId());
    if (validTokens.isEmpty()) {
      return;
    }

    validTokens.forEach(t -> t.setLoggedOut(true));

    tokenRepository.saveAll(validTokens);
  }

  default void saveUserToken(String jwt, UserEntity user, TokenRepository tokenRepository) {
    Token token = new Token();
    token.setToken(jwt);
    token.setLoggedOut(false);
    token.setUserEntity(user);
    tokenRepository.save(token);
  }
}
