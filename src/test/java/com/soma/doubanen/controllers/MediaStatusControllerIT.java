package com.soma.doubanen.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soma.doubanen.DataUtil;
import com.soma.doubanen.domains.dto.MediaStatusDto;
import com.soma.doubanen.domains.entities.AuthorEntity;
import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.entities.MediaStatusEntity;
import com.soma.doubanen.domains.enums.MediaStatus;
import com.soma.doubanen.domains.enums.MediaType;
import com.soma.doubanen.services.AuthService;
import com.soma.doubanen.services.MediaService;
import com.soma.doubanen.services.MediaStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class MediaStatusControllerIT {
  private final MediaService mediaService;

  private final MediaStatusService mediaStatusService;

  private final AuthService authService;

  private final MockMvc mockMvc;

  private final ObjectMapper objectMapper;

  @Autowired
  public MediaStatusControllerIT(
      MediaService mediaService,
      MediaStatusService mediaStatusService,
      AuthService authService,
      MockMvc mockMvc,
      ObjectMapper objectMapper) {
    this.mediaService = mediaService;
    this.mediaStatusService = mediaStatusService;
    this.authService = authService;
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
  }

  @Test
  public void TestThatCreateMediaStatusSuccessfullyReturnsHttpCreated() throws Exception {
    AuthorEntity authorEntity1 = DataUtil.CreateArtistPunkAndRock();
    authorEntity1.setId(null);
    MediaEntity mediaEntity1 = DataUtil.CreateMusicRockAlbum(authorEntity1);
    mediaService.save(mediaEntity1, null);
    MediaStatusDto mediaStatusDto =
        MediaStatusDto.builder()
            .mediaId(1L)
            .status(MediaStatus.Rated)
            .score(3f)
            .type(MediaType.Music)
            .userId(1L)
            .build();
    String mediaStatusJson = objectMapper.writeValueAsString(mediaStatusDto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/media-statuses")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + DataUtil.obtainAdminAccessToken(authService))
                .content(mediaStatusJson))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value("1"));
  }

  @Test
  public void TestThatCreateMediaStatusWithIncorrectUserIdReturnsHttpUnAuthorized()
      throws Exception {
    AuthorEntity authorEntity1 = DataUtil.CreateArtistPunkAndRock();
    authorEntity1.setId(null);
    MediaEntity mediaEntity1 = DataUtil.CreateMusicRockAlbum(authorEntity1);
    mediaService.save(mediaEntity1, null);
    MediaStatusDto mediaStatusDto =
        MediaStatusDto.builder()
            .mediaId(1L)
            .status(MediaStatus.Rated)
            .score(3f)
            .type(MediaType.Music)
            .userId(2L)
            .build();
    String mediaStatusJson = objectMapper.writeValueAsString(mediaStatusDto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/media-statuses")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + DataUtil.obtainAdminAccessToken(authService))
                .content(mediaStatusJson))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  public void TestThatCreateMediaStatusWithIncorrectMediaIdReturnsHttpBadRequest()
      throws Exception {
    AuthorEntity authorEntity1 = DataUtil.CreateArtistPunkAndRock();
    authorEntity1.setId(null);
    MediaEntity mediaEntity1 = DataUtil.CreateMusicRockAlbum(authorEntity1);
    mediaService.save(mediaEntity1, null);
    MediaStatusDto mediaStatusDto =
        MediaStatusDto.builder()
            .mediaId(2L)
            .status(MediaStatus.Rated)
            .score(3f)
            .type(MediaType.Music)
            .userId(1L)
            .build();
    String mediaStatusJson = objectMapper.writeValueAsString(mediaStatusDto);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/media-statuses")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + DataUtil.obtainStandardAccessToken(authService))
                .content(mediaStatusJson))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void TestThatGetUserMediaStatusSuccessfullyReturnsHttpOk() throws Exception {
    AuthorEntity authorEntity1 = DataUtil.CreateArtistPunkAndRock();
    authorEntity1.setId(null);
    MediaEntity mediaEntity1 = DataUtil.CreateMusicRockAlbum(authorEntity1);
    mediaService.save(mediaEntity1, null);
    DataUtil.obtainAdminAccessToken(authService);
    MediaStatusEntity mediaStatusEntity =
        MediaStatusEntity.builder()
            .mediaId(1L)
            .score(3f)
            .type(MediaType.Music)
            .userId(1L)
            .status(MediaStatus.Rated)
            .build();
    mediaStatusService.save(mediaStatusEntity, null);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/media-statuses?userId=1&mediaId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("Rated"));
  }

  @Test
  public void TestThatGetUserCurrentOnSuccessfullyReturnsHttpOk() throws Exception {
    AuthorEntity artist = DataUtil.CreateArtistPunkAndRock();
    AuthorEntity director = DataUtil.CreateDirector();
    AuthorEntity author = DataUtil.CreateAuthor();
    artist.setId(null);
    director.setId(null);
    author.setId(null);
    MediaEntity music = DataUtil.CreateMusicRockAlbum(artist);
    MediaEntity movie = DataUtil.CreateMovie(director);
    MediaEntity book = DataUtil.CreateBook(author);
    mediaService.save(music, null);
    mediaService.save(movie, null);
    mediaService.save(book, null);
    DataUtil.obtainStandardAccessToken(authService);
    mediaStatusService.save(
        MediaStatusEntity.builder()
            .mediaId(1L)
            .score(3f)
            .type(MediaType.Music)
            .userId(1L)
            .status(MediaStatus.Rated)
            .build(),
        null);
    mediaStatusService.save(
        MediaStatusEntity.builder()
            .mediaId(2L)
            .score(3f)
            .type(MediaType.Movie)
            .userId(1L)
            .status(MediaStatus.Rated)
            .build(),
        null);
    mediaStatusService.save(
        MediaStatusEntity.builder()
            .mediaId(3L)
            .score(3f)
            .type(MediaType.Book)
            .userId(1L)
            .status(MediaStatus.Rated)
            .build(),
        null);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/media-statuses?userId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title").value("Nevermind"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].title").value("Movie"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[2].title").value("Book"));
  }
}
