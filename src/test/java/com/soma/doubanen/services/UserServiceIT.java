package com.soma.doubanen.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.soma.doubanen.DataUtil;
import com.soma.doubanen.domains.entities.UserEntity;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceIT {

  private final UserService userService;

  @Autowired
  public UserServiceIT(UserService userService) {
    this.userService = userService;
  }

  @Test
  public void UserServiceCanSaveFindAll() {
    UserEntity userEntity1 = DataUtil.CreateUserA();
    UserEntity userEntity2 = DataUtil.CreateUserB();
    userService.save(userEntity1);
    userService.save(userEntity2);
    List<UserEntity> userEntities = Arrays.asList(userEntity1, userEntity2);
    List<UserEntity> findEntities = userService.findAll();
    findEntities.forEach(userEntity -> userEntity.setTokens(null));
    assertThat(findEntities).isEqualTo(userEntities);
  }
}
