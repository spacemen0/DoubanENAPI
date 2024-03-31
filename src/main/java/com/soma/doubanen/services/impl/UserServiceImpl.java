package com.soma.doubanen.services.impl;

import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.repositories.MediaStatusRepository;
import com.soma.doubanen.repositories.TokenRepository;
import com.soma.doubanen.repositories.UserRepository;
import com.soma.doubanen.services.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final TokenRepository tokenRepository;

  private final MediaStatusRepository mediaStatusRepository;

  public UserServiceImpl(
      UserRepository userRepository,
      TokenRepository tokenRepository,
      MediaStatusRepository mediaStatusRepository) {
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
    this.mediaStatusRepository = mediaStatusRepository;
  }

  @Override
  public UserEntity save(UserEntity userEntity) {
    return userRepository.save(userEntity);
  }

  @Override
  public Optional<UserEntity> findOne(Long id) {
    return userRepository.findById(id);
  }

  @Override
  public String getUsernameById(Long id) {
    return userRepository.findUsernameById(id).orElse("");
  }

  @Override
  public List<UserEntity> findAll() {
    return userRepository.findAll();
  }

  @Override
  public boolean notExists(Long id) {
    return !userRepository.existsById(id);
  }

  @Override
  public void delete(Long id) {
    mediaStatusRepository.deleteAllByUserId(id);
    tokenRepository.deleteAllByUserEntityId(id);
    userRepository.deleteById(id);
  }

  @Override
  public UserEntity partialUpdate(UserEntity userEntity, Long id) {
    userEntity.setId(id);
    return userRepository
        .findById(id)
        .map(
            existingUser -> {
              Optional.ofNullable(userEntity.getEmail()).ifPresent(existingUser::setEmail);
              Optional.ofNullable(userEntity.getPassword()).ifPresent(existingUser::setPassword);
              Optional.ofNullable(userEntity.getUsername()).ifPresent(existingUser::setUsername);
              return existingUser;
            })
        .orElseThrow(() -> new RuntimeException("User not fund"));
  }
}
