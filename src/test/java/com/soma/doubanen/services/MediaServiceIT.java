package com.soma.doubanen.services;

import static org.assertj.core.api.Assertions.assertThat;

import com.soma.doubanen.DataUtil;
import com.soma.doubanen.domains.entities.AuthorEntity;
import com.soma.doubanen.domains.entities.MediaEntity;
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
public class MediaServiceIT {

  private final MediaService mediaService;

  @Autowired
  public MediaServiceIT(MediaService mediaService) {
    this.mediaService = mediaService;
  }

  @Test
  public void MusicServiceCanSaveFindAll() {
    AuthorEntity authorEntity1 = DataUtil.CreateArtistPunkAndRock();
    AuthorEntity authorEntity2 = DataUtil.CreateArtistExperimentalAndNoise();
    authorEntity1.setId(null);
    authorEntity2.setId(null);
    MediaEntity mediaEntity1 = DataUtil.CreateMusicRockAlbum(authorEntity1);
    MediaEntity mediaEntity2 = DataUtil.CreateMusicExperimentalEP(authorEntity2);
    mediaService.save(mediaEntity1, null);
    mediaService.save(mediaEntity2, null);
    List<MediaEntity> musicEntities = Arrays.asList(mediaEntity1, mediaEntity2);
    assertThat(mediaService.findAll().toString()).isEqualTo(musicEntities.toString());
  }
}
