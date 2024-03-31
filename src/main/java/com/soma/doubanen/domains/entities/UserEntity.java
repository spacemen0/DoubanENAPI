package com.soma.doubanen.domains.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.soma.doubanen.domains.auth.Token;
import com.soma.doubanen.domains.enums.UserRole;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "users")
@EqualsAndHashCode
public class UserEntity implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(unique = true, nullable = false)
  private String email;

  @Enumerated(EnumType.STRING)
  private UserRole role;

  @Column(nullable = false)
  private String password;

  private LocalDate date;

  private String profileImageUrl;

  @Column(columnDefinition = "TEXT")
  private String bio;

  @OneToMany(mappedBy = "userEntity", fetch = FetchType.EAGER)
  @JsonIgnore
  private List<Token> tokens;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
