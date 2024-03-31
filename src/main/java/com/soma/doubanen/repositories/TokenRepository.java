package com.soma.doubanen.repositories;

import com.soma.doubanen.domains.auth.Token;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TokenRepository extends JpaRepository<Token, Long> {

  @Query(
      """
            select t from Token t inner join UserEntity u on t.userEntity.id = u.id
            where t.userEntity.id = :userId and t.loggedOut = false
            """)
  List<Token> findAllTokensByUser(Long userId);

  @Transactional
  void deleteAllByUserEntityId(Long userId);

  Optional<Token> findByToken(String token);
}
