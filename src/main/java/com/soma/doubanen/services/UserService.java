package com.soma.doubanen.services;

import com.soma.doubanen.domains.entities.UserEntity;
import java.util.List;
import java.util.Optional;

public interface UserService {

  UserEntity save(UserEntity userEntity);

  Optional<UserEntity> findOne(Long id);

  String getUsernameById(Long id);

  List<UserEntity> findAll();

  boolean notExists(Long id);

  void delete(Long id);

  UserEntity partialUpdate(UserEntity userEntity, Long id);
}
