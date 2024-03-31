package com.soma.doubanen.services.impl;

import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.repositories.TokenRepository;
import com.soma.doubanen.services.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

  private final TokenRepository tokenRepository;

  public TokenServiceImpl(TokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;
  }

  @Override
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  @Override
  public boolean isValid(String token, UserDetails user) {
    String username = extractUsername(token);

    boolean validToken =
        tokenRepository.findByToken(token).map(t -> !t.isLoggedOut()).orElse(false);

    return (username.equals(user.getUsername())) && !isTokenExpired(token) && validToken;
  }

  @Override
  public <T> T extractClaim(String token, Function<Claims, T> resolver) {
    Claims claims = extractAllClaims(token);
    return resolver.apply(claims);
  }

  @Override
  public String generateToken(UserEntity user) {
    return Jwts.builder()
        .subject(user.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
        .signWith(getSignInKey())
        .compact();
  }
}
