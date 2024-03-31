package com.soma.doubanen.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.soma.doubanen.DataUtil;
import com.soma.doubanen.domains.entities.AuthorEntity;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthorServiceIT {
  private final AuthorService authorService;

  @Autowired
  public AuthorServiceIT(AuthorService authorService) {
    this.authorService = authorService;
  }

  @Test
  public void ArtistServiceCanSaveFindAll() {
    AuthorEntity authorEntity1 = DataUtil.CreateArtistPunkAndRock();
    AuthorEntity authorEntity2 = DataUtil.CreateArtistExperimentalAndNoise();
    authorService.save(authorEntity1, null);
    authorService.save(authorEntity2, null);
    List<AuthorEntity> authorEntities = Arrays.asList(authorEntity1, authorEntity2);
    List<AuthorEntity> findArtists = authorService.findAll();
    findArtists.forEach(authorEntity -> authorEntity.setMediaEntities(null));
    assertThat(findArtists).isEqualTo(authorEntities);
  }
}
