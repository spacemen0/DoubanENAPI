package com.soma.doubanen.controllers;

import com.soma.doubanen.domains.auth.AuthResponse;
import com.soma.doubanen.domains.dto.UserDto;
import com.soma.doubanen.domains.entities.ImageEntity;
import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.domains.enums.ImageType;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.AuthService;
import com.soma.doubanen.services.ImageService;
import com.soma.doubanen.services.TokenService;
import com.soma.doubanen.services.UserService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  private final AuthService authService;

  private final ImageService imageService;

  private final TokenService tokenService;
  private final Mapper<UserEntity, UserDto> userMapper;

  private final PasswordEncoder passwordEncoder;

  public UserController(
      UserService userService,
      AuthService authService,
      ImageService imageService,
      TokenService tokenService,
      Mapper<UserEntity, UserDto> userMapper,
      PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.authService = authService;
    this.imageService = imageService;
    this.tokenService = tokenService;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping
  public ResponseEntity<AuthResponse> createUser(@RequestBody UserDto userDto) {
    UserEntity userEntity = userMapper.mapFrom(userDto);
    userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
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
      @PathVariable("id") Long id,
      @RequestBody UserDto userDto,
      @RequestHeader(name = "Authorization") String auth) {
    if (userService.notExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(id)))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    userDto.setId(id);
    UserEntity userEntity = userMapper.mapFrom(userDto);
    if (userEntity.getPassword() != null) {
      userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
    }
    UserEntity savedUserEntity = userService.save(userEntity);
    return new ResponseEntity<>(userMapper.mapTo(savedUserEntity), HttpStatus.OK);
  }

  @PatchMapping(
      path = "/{id}",
      consumes = {"multipart/form-data"})
  public ResponseEntity<?> partialUpdateUser(
      @PathVariable("id") Long id,
      @ModelAttribute UserDto userDto,
      @RequestParam(value = "image", required = false) MultipartFile image,
      @RequestHeader(name = "Authorization") String auth) {
    if (userService.notExists(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(id)))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    ImageEntity savedImage = null;
    if (image != null) {
      byte[] data;
      try {
        data = imageService.compressImage(image.getBytes());
      } catch (IOException e) {
        return new ResponseEntity<>("Error compressing image", HttpStatus.INTERNAL_SERVER_ERROR);
      }
      ImageEntity imageEntity =
          ImageEntity.builder().imageData(data).objectId(id).type(ImageType.UserProfile).build();
      savedImage = imageService.save(imageEntity);
    }
    UserEntity userEntity = userMapper.mapFrom(userDto);
    if (userEntity.getPassword() != null) {
      userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
    }
    if (savedImage != null) {
      userEntity.setProfileImageUrl("/images/" + savedImage.getId());
    }
    UserEntity updatedUser = userService.partialUpdate(userEntity, id);
    return new ResponseEntity<>(userMapper.mapTo(updatedUser), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(
      @PathVariable("id") Long id, @RequestHeader(name = "Authorization") String auth) {
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(id)))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    userService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping("/check-password")
  public ResponseEntity<Boolean> checkPassword(
      @RequestBody UserDto userDto, @RequestHeader(name = "Authorization") String auth) {
    String token = auth.substring(7);
    String username = tokenService.extractUsername(token);
    if (!username.equals(userService.getUsernameById(userDto.getId())))
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    if (userService.checkPassword(userDto.getId(), userDto.getPassword())) {
      return new ResponseEntity<>(true, HttpStatus.OK);
    }
    return new ResponseEntity<>(false, HttpStatus.FORBIDDEN);
  }
}
