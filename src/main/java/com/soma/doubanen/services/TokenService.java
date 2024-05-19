package com.soma.doubanen.services;

import com.soma.doubanen.domains.entities.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.security.core.userdetails.UserDetails;

public interface TokenService {

  String extractUsername(String token);

  boolean isValid(String token, UserDetails user);

  default boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  default Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  <T> T extractClaim(String token, Function<Claims, T> resolver);

  default Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
  }

  String generateToken(UserEntity user);

  @SuppressWarnings("SpellCheckingInspection")
  default SecretKey getSignInKey() {
    String SECRET_KEY = "4bb6d1dfbafb64a681139d1586b6f1160d18159afd57c8c79136d749079fm4do";
    byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
