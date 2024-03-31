package com.soma.doubanen.controllers;

import com.soma.doubanen.domains.auth.AuthResponse;
import com.soma.doubanen.domains.dto.UserDto;
import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.AuthService;
import com.soma.doubanen.services.UserService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  private final AuthService authService;
  private final Mapper<UserEntity, UserDto> userMapper;

  public UserController(
      UserService userService, AuthService authService, Mapper<UserEntity, UserDto> userMapper) {
    this.userService = userService;
    this.authService = authService;
    this.userMapper = userMapper;
  }

  @PostMapping
  public ResponseEntity<AuthResponse> createUser(@RequestBody UserDto userDto) {
    UserEntity userEntity = userMapper.mapFrom(userDto);
    return new ResponseEntity<>(authService.register(userEntity), HttpStatus.CREATED);
  }

  @GetMapping
  public List<UserDto> listUsers() {
    List<UserEntity> userEntities = userService.findAll();
    return userEntities.stream().map(userMapper::mapTo).collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> getUser(@PathVariable("id") Long id) {
    Optional<UserEntity> foundUser = userService.findOne(id);
    return foundUser
        .map(userEntity -> new ResponseEntity<>(userMapper.mapTo(userEntity), HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserDto> updateUser(
      @PathVariable("id") Long id, @RequestBody UserDto userDto) {
    if (userService.notExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    userDto.setId(id);
    UserEntity userEntity = userMapper.mapFrom(userDto);
    UserEntity savedUserEntity = userService.save(userEntity);
    return new ResponseEntity<>(userMapper.mapTo(savedUserEntity), HttpStatus.OK);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<UserDto> partialUpdateUser(
      @PathVariable("id") Long id, @RequestBody UserDto userDto) {
    if (userService.notExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    UserEntity userEntity = userMapper.mapFrom(userDto);
    UserEntity updatedUser = userService.partialUpdate(userEntity, id);
    return new ResponseEntity<>(userMapper.mapTo(updatedUser), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
    userService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
