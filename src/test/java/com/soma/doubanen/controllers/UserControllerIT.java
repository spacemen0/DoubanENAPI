package com.soma.doubanen.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soma.doubanen.DataUtil;
import com.soma.doubanen.domains.dto.UserDto;
import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.AuthService;
import com.soma.doubanen.services.UserService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserControllerIT {

  private final UserService userService;

  private final AuthService authService;

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;
  private final Mapper<UserEntity, UserDto> userMapper;

  @Autowired
  public UserControllerIT(
      UserService userService,
      AuthService authService,
      MockMvc mockMvc,
      ObjectMapper objectMapper,
      Mapper<UserEntity, UserDto> userMapper) {
    this.userService = userService;
    this.authService = authService;
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
    this.userMapper = userMapper;
  }

  @Test
  public void createUserSuccessfullyReturnsHTTPCreated() throws Exception {
    UserEntity userEntity = DataUtil.CreateUserA();
    UserDto userDto = userMapper.mapTo(userEntity);
    userDto.setPassword("123456789");
    String userJson = objectMapper.writeValueAsString(userDto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + DataUtil.obtainAdminAccessToken(authService))
                .content(userJson))
        .andExpect(MockMvcResultMatchers.status().isCreated());
    assertThat(userService.findOne(1L)).isPresent();
  }

  @Test
  public void createMultipleUsersSuccessfullyReturnsHTTPCreated() throws Exception {
    UserEntity userEntity1 = DataUtil.CreateUserA();
    UserEntity userEntity2 = DataUtil.CreateUserB();
    userService.save(userEntity1);
    userService.save(userEntity2);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].username").value("Abe"));
  }

  @Test
  public void getUserSuccessfullyReturnsHTTPOK() throws Exception {
    UserEntity userEntity = DataUtil.CreateUserA();
    UserEntity savedUser = userService.save(userEntity);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/" + savedUser.getId()))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void updateUserSuccessfullyReturnsHTTPOK() throws Exception {
    String token = DataUtil.obtainAdminAccessToken(authService);
    Optional<UserEntity> ifSavedUser = userService.findOne(1L);
    UserEntity savedUser = ifSavedUser.orElseThrow();
    savedUser.setUsername("UpdatedName");
    UserDto userDto = userMapper.mapTo(savedUser);
    userDto.setPassword("12345678");
    String userJson = objectMapper.writeValueAsString(userDto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/users/" + savedUser.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
        .andExpect(MockMvcResultMatchers.status().isOk());
    Optional<UserEntity> foundUser = userService.findOne(1L);
    assertThat(foundUser).isPresent();
    assertThat((foundUser).get().getUsername()).isEqualTo("UpdatedName");
  }

  @Test
  public void partiallyUpdateUserSuccessfullyReturnsHTTPOK() throws Exception {
    String token = DataUtil.obtainAdminAccessToken(authService);
    Optional<UserEntity> userEntity = userService.findOne(1L);
    assertThat(userEntity).isPresent();
    UserEntity savedUser = userEntity.get();
    savedUser.setUsername("UpdatedName");
    MockMultipartFile mockFile =
        new MockMultipartFile("image", "filename.txt", "text/plain", "some image data".getBytes());
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("username", savedUser.getUsername());
    mockMvc
        .perform(
            MockMvcRequestBuilders.multipart(HttpMethod.PATCH, "/users/" + savedUser.getId())
                .file(mockFile)
                .params(formData)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(MockMvcResultMatchers.status().isOk());
    Optional<UserEntity> foundUser = userService.findOne(1L);
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getUsername()).isEqualTo("UpdatedName");
  }
}
