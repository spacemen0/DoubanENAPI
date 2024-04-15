package com.soma.doubanen.controllers;

import com.soma.doubanen.repositories.ReviewRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ReviewControllerIT {
  private final ReviewRepository reviewRepository;

  @Autowired
  public ReviewControllerIT(ReviewRepository reviewRepository) {
    this.reviewRepository = reviewRepository;
  }
}
