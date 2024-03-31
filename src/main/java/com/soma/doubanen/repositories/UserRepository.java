package com.soma.doubanen.repositories;

import com.soma.doubanen.domains.entities.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByUsername(String name);

  Optional<UserEntity> findByEmail(String email);

  @Query("SELECT u.username FROM UserEntity u WHERE u.id = :id")
  Optional<String> findUsernameById(@Param("id") Long id);
}
