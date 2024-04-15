package com.soma.doubanen.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soma.doubanen.DataUtil;
import com.soma.doubanen.domains.dto.MediaDto;
import com.soma.doubanen.domains.entities.AuthorEntity;
import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.entities.MediaStatusEntity;
import com.soma.doubanen.domains.enums.MediaStatus;
import com.soma.doubanen.mappers.Mapper;
import com.soma.doubanen.services.AuthService;
import com.soma.doubanen.services.MediaService;
import com.soma.doubanen.services.MediaStatusService;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
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
public class MediaControllerIT {

  private final MediaService mediaService;

  private final AuthService authService;
  private final MediaStatusService mediaStatusService;

  private final MockMvc mockMvc;

  private final ObjectMapper objectMapper;

  private final Mapper<MediaEntity, MediaDto> musicMapper;

  @Autowired
  public MediaControllerIT(
      MediaService mediaService,
      AuthService authService,
      MediaStatusService mediaStatusService,
      MockMvc mockMvc,
      ObjectMapper objectMapper,
      Mapper<MediaEntity, MediaDto> musicMapper) {
    this.mediaService = mediaService;
    this.authService = authService;
    this.mediaStatusService = mediaStatusService;
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
    this.musicMapper = musicMapper;
  }

  private static MultiValueMap<String, String> getMultiValueMap(MediaEntity mediaEntity) {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("title", mediaEntity.getTitle());
    formData.add("description", mediaEntity.getDescription());
    formData.add("releaseDate", mediaEntity.getReleaseDate().toString());
    formData.add("average", "0");
    formData.add("ratings", "0");
    formData.add("wants", "0");
    formData.add("doings", "0");
    formData.add("additional", "0");
    formData.add("genre", "Pop");
    formData.add("imageUrl", "PlaceHolder");
    formData.add("type", "Music");
    formData.add("authorName", "0");
    formData.add("average", "0");
    formData.add("authorType", "Artist");
    return formData;
  }

  @Test
  public void CreateMediaSuccessfullyReturnsHTTPCreated() throws Exception {
    AuthorEntity authorEntity = DataUtil.CreateArtistPunkAndRock();
    MediaEntity mediaEntity = DataUtil.CreateMusicRockAlbum(authorEntity);
    mediaEntity.setId(null);
    mediaEntity.setReleaseDate(LocalDate.of(2012, 12, 2));
    MultiValueMap<String, String> formData = getMultiValueMap(mediaEntity);
    MockMultipartFile mockFile =
        new MockMultipartFile("image", "filename.txt", "text/plain", "some image data".getBytes());
    mockMvc
        .perform(
            MockMvcRequestBuilders.multipart("/medias")
                .file(mockFile)
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + DataUtil.obtainAdminAccessToken(authService))
                .params(formData))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("2012-12-02"));

    Optional<MediaEntity> foundMusic = mediaService.findOne(1L);
    assertThat(foundMusic).isPresent();
  }

  @Test
  public void GetMediaSuccessfullyReturnsHTTPOK() throws Exception {
    AuthorEntity authorEntity = DataUtil.CreateArtistPunkAndRock();
    authorEntity.setId(null);
    MediaEntity mediaEntity = DataUtil.CreateMusicRockAlbum(authorEntity);
    mediaEntity.setId(null);
    Optional<MediaEntity> savedMusicEntity = mediaService.save(mediaEntity, null);
    assertThat(savedMusicEntity).isPresent();
    mockMvc
        .perform(MockMvcRequestBuilders.get("/medias/" + savedMusicEntity.get().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Nevermind"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value("Rock"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void MediaCanBeUpdated() throws Exception {
    AuthorEntity authorEntity = DataUtil.CreateArtistPunkAndRock();
    authorEntity.setId(null);
    MediaEntity mediaEntity = DataUtil.CreateMusicRockAlbum(authorEntity);
    mediaEntity.setId(null);
    Optional<MediaEntity> savedMusicEntity = mediaService.save(mediaEntity, null);
    assertThat(savedMusicEntity).isPresent();
    authorEntity.setName("Sonic Youth");
    authorEntity.setId(1L);
    mediaEntity.setAuthorEntity(authorEntity);
    assertThat(mediaEntity.getAuthorEntity()).isEqualTo(authorEntity);
    mediaEntity.setDescription("Motown");
    mediaEntity.setType(com.soma.doubanen.domains.enums.MediaType.Music);
    String musicJson = objectMapper.writeValueAsString(musicMapper.mapTo(mediaEntity));
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/medias/" + savedMusicEntity.get().getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + DataUtil.obtainAdminAccessToken(authService))
                .content(musicJson))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Nevermind"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Motown"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("Music"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void MediaCanBePartiallyUpdated() throws Exception {
    AuthorEntity authorEntity = DataUtil.CreateArtistPunkAndRock();
    authorEntity.setId(null);
    MediaEntity mediaEntity = DataUtil.CreateMusicRockAlbum(authorEntity);
    mediaEntity.setAdditionalInfo("Tracks: Many Tracks");
    mediaEntity.setDescription("Motown");
    Optional<MediaEntity> savedMusicEntity = mediaService.save(mediaEntity, null);
    assertThat(savedMusicEntity).isPresent();
    mediaEntity.setAuthorEntity(authorEntity);
    mediaEntity.setDescription(null);
    mediaEntity.setAdditionalInfo(null);
    String musicJson = objectMapper.writeValueAsString(musicMapper.mapTo(mediaEntity));
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/medias/" + savedMusicEntity.get().getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + DataUtil.obtainAdminAccessToken(authService))
                .content(musicJson))
        .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Nevermind"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Motown"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.additional").value("Tracks: Many Tracks"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void DeletingMediaReturnsHTTPNoContent() throws Exception {
    AuthorEntity authorEntity = DataUtil.CreateArtistPunkAndRock();
    authorEntity.setId(null);
    MediaEntity mediaEntity = DataUtil.CreateMusicRockAlbum(authorEntity);
    mediaEntity.setId(null);
    Optional<MediaEntity> savedMusicEntity = mediaService.save(mediaEntity, null);
    assertThat(savedMusicEntity).isPresent();
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/medias/" + savedMusicEntity.get().getId())
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + DataUtil.obtainAdminAccessToken(authService)))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
    Optional<MediaEntity> noMusicEntity = mediaService.findOne(savedMusicEntity.get().getId());
    assertThat(noMusicEntity).isEmpty();
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
            .type(com.soma.doubanen.domains.enums.MediaType.Music)
            .userId(1L)
            .status(MediaStatus.Rated)
            .build(),
        null);
    mediaStatusService.save(
        MediaStatusEntity.builder()
            .mediaId(2L)
            .score(3f)
            .type(com.soma.doubanen.domains.enums.MediaType.Movie)
            .userId(1L)
            .status(MediaStatus.Rated)
            .build(),
        null);
    mediaStatusService.save(
        MediaStatusEntity.builder()
            .mediaId(3L)
            .score(3f)
            .type(com.soma.doubanen.domains.enums.MediaType.Book)
            .userId(1L)
            .status(MediaStatus.Rated)
            .build(),
        null);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/medias?userId=1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].title").value("Nevermind"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].title").value("Movie"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[2].title").value("Book"));
  }
}
