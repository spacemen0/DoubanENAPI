package com.soma.doubanen.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soma.doubanen.DataUtil;
import com.soma.doubanen.domains.dto.AuthorDto;
import com.soma.doubanen.domains.entities.AuthorEntity;
import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.AuthService;
import com.soma.doubanen.services.AuthorService;
import com.soma.doubanen.services.MediaService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AuthorControllerIT {

  private final MediaService mediaService;

  private final AuthorService authorService;

  private final AuthService authService;

  private final MockMvc mockMvc;

  private final ObjectMapper objectMapper;

  private final Mapper<AuthorEntity, AuthorDto> authorMapper;

  @Autowired
  public AuthorControllerIT(
      MediaService mediaService,
      AuthorService authorService,
      AuthService authService,
      MockMvc mockMvc,
      ObjectMapper objectMapper,
      Mapper<AuthorEntity, AuthorDto> authorMapper) {
    this.mediaService = mediaService;
    this.authorService = authorService;
    this.authService = authService;
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
    this.authorMapper = authorMapper;
  }

  @Test
  public void CreateArtistSuccessfullyReturnsHTTPCreated() throws Exception {
    AuthorEntity authorEntity = DataUtil.CreateArtistExperimentalAndNoise();
    String authorJson = objectMapper.writeValueAsString(authorMapper.mapTo(authorEntity));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + DataUtil.obtainAdminAccessToken(authService))
                .content(authorJson))
        .andExpect(MockMvcResultMatchers.status().isCreated());
    assertThat(authorService.findOne(1L)).isPresent();
  }

  @Test
  public void CreateMultipleArtistSuccessfullyReturnsHTTPCreated() throws Exception {
    AuthorEntity authorEntity1 = DataUtil.CreateArtistExperimentalAndNoise();
    AuthorEntity authorEntity2 = DataUtil.CreateArtistExperimentalAndNoise();
    authorService.save(authorEntity1, null);
    authorService.save(authorEntity2, null);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/authors"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value("swans"));
  }

  @Test
  public void GetArtistSuccessfullyReturnsHTTPOK() throws Exception {
    AuthorEntity authorEntity = DataUtil.CreateArtistExperimentalAndNoise();
    AuthorEntity savedArtist = authorService.save(authorEntity, null);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/authors/" + savedArtist.getId()))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void UpdateArtistSuccessfullyReturnsHTTPOK() throws Exception {
    AuthorEntity authorEntity = DataUtil.CreateArtistExperimentalAndNoise();
    AuthorEntity savedArtist = authorService.save(authorEntity, null);
    savedArtist.setName("Guru Guru");
    String authorJson = objectMapper.writeValueAsString(authorMapper.mapTo(savedArtist));
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/authors/" + savedArtist.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + DataUtil.obtainAdminAccessToken(authService))
                .content(authorJson))
        .andExpect(MockMvcResultMatchers.status().isOk());
    Optional<AuthorEntity> foundArtist = authorService.findOne(savedArtist.getId());
    assertThat(foundArtist).isPresent();
    assertThat(foundArtist.get().getName()).isEqualTo("Guru Guru");
  }

  @Test
  public void PartiallyUpdateArtistSuccessfullyReturnsHTTPOK() throws Exception {
    AuthorEntity authorEntity = DataUtil.CreateArtistExperimentalAndNoise();
    authorEntity.setName("Guru Guru");
    AuthorEntity savedArtist = authorService.save(authorEntity, null);
    savedArtist.setName(null);
    String authorJson = objectMapper.writeValueAsString(authorMapper.mapTo(savedArtist));
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/authors/" + savedArtist.getId())
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + DataUtil.obtainAdminAccessToken(authService))
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson))
        .andExpect(MockMvcResultMatchers.status().isOk());
    Optional<AuthorEntity> foundArtist = authorService.findOne(savedArtist.getId());
    assertThat(foundArtist).isPresent();
    assertThat(foundArtist.get().getName()).isEqualTo("Guru Guru");
  }

  @Test
  public void DeletingArtistAlsoDeleteAllTheirMusics() throws Exception {
    AuthorEntity authorEntity = DataUtil.CreateArtistPunkAndRock();
    authorEntity.setId(null);
    MediaEntity mediaEntity = DataUtil.CreateMusicRockAlbum(authorEntity);
    Optional<MediaEntity> savedMusicEntity1 = mediaService.save(mediaEntity, null);
    Optional<MediaEntity> savedMusicEntity2 = mediaService.save(mediaEntity, null);
    assertThat(savedMusicEntity1).isPresent();
    assertThat(savedMusicEntity2).isPresent();
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(
                    "/authors/" + savedMusicEntity1.get().getAuthorEntity().getId())
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + DataUtil.obtainAdminAccessToken(authService)))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
    List<MediaEntity> noMediaEntity = mediaService.findAll();
    assertThat(noMediaEntity).isEmpty();
  }
}
