package com.soma.doubanen.domains.auth;

import com.soma.doubanen.domains.entities.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "tokens")
@EqualsAndHashCode
public class Token {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String token;

  private boolean loggedOut;

  @ManyToOne()
  @JoinColumn(name = "user_id")
  private UserEntity userEntity;
}
