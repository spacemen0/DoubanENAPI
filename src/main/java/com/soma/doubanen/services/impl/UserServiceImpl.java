package com.soma.doubanen.services.impl;

import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.domains.enums.ImageType;
import com.soma.doubanen.repositories.ImageRepository;
import com.soma.doubanen.repositories.MediaStatusRepository;
import com.soma.doubanen.repositories.TokenRepository;
import com.soma.doubanen.repositories.UserRepository;
import com.soma.doubanen.services.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  private final TokenRepository tokenRepository;

  private final ImageRepository imageRepository;

  private final MediaStatusRepository mediaStatusRepository;

  private final PasswordEncoder passwordEncoder;

  public UserServiceImpl(
      UserRepository userRepository,
      TokenRepository tokenRepository,
      ImageRepository imageRepository,
      MediaStatusRepository mediaStatusRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
    this.imageRepository = imageRepository;
    this.mediaStatusRepository = mediaStatusRepository;
    this.passwordEncoder = passwordEncoder;
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
    imageRepository.deleteAllByObjectIdAndType(id, ImageType.UserProfile);
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
              Optional.ofNullable(userEntity.getBio()).ifPresent(existingUser::setBio);
              Optional.ofNullable(userEntity.getProfileImageUrl())
                  .ifPresent(existingUser::setProfileImageUrl);
              return userRepository.save(existingUser);
            })
        .orElseThrow(() -> new RuntimeException("User not fund"));
  }

  @Override
  public boolean checkPassword(Long id, String password) {
    Optional<UserEntity> userEntity = userRepository.findById(id);
    return userEntity
        .map(user -> passwordEncoder.matches(password, user.getPassword()))
        .orElse(false);
  }
}
