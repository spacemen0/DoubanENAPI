package com.soma.doubanen;

import com.soma.doubanen.domains.auth.AuthResponse;
import com.soma.doubanen.domains.entities.AuthorEntity;
import com.soma.doubanen.domains.entities.MediaEntity;
import com.soma.doubanen.domains.entities.UserEntity;
import com.soma.doubanen.domains.enums.AuthorType;
import com.soma.doubanen.domains.enums.MediaGenre;
import com.soma.doubanen.domains.enums.MediaType;
import com.soma.doubanen.domains.enums.UserRole;
import com.soma.doubanen.services.AuthService;
import java.time.LocalDate;
import java.util.Arrays;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public final class DataUtil {

  public static MediaEntity CreateMusicRockAlbum(AuthorEntity authorEntity) {
    return MediaEntity.builder()
        .id(1L)
        .genre(MediaGenre.Rock)
        .title("Nevermind")
        .releaseDate(LocalDate.now())
        .average(0f)
        .doings(0L)
        .wants(9L)
        .type(MediaType.Music)
        .ratings(0L)
        .imageUrl("image")
        .authorEntity(authorEntity)
        .build();
  }

  public static MediaEntity CreateMovie(AuthorEntity authorEntity) {
    return MediaEntity.builder()
        .id(1L)
        .genre(MediaGenre.Mystery)
        .title("Movie")
        .releaseDate(LocalDate.now())
        .average(0f)
        .doings(0L)
        .wants(9L)
        .type(MediaType.Movie)
        .ratings(0L)
        .imageUrl("image")
        .authorEntity(authorEntity)
        .build();
  }

  public static MediaEntity CreateBook(AuthorEntity authorEntity) {
    return MediaEntity.builder()
        .id(1L)
        .genre(MediaGenre.Mystery)
        .title("Book")
        .releaseDate(LocalDate.now())
        .average(0f)
        .doings(0L)
        .wants(9L)
        .type(MediaType.Book)
        .ratings(0L)
        .imageUrl("image")
        .authorEntity(authorEntity)
        .build();
  }

  public static MediaEntity CreateMusicExperimentalEP(AuthorEntity authorEntity) {
    return MediaEntity.builder()
        .id(2L)
        .genre(MediaGenre.Experimental)
        .title("Die Tür ist zu")
        .releaseDate(LocalDate.now())
        .average(0f)
        .doings(0L)
        .wants(9L)
        .type(MediaType.Music)
        .ratings(0L)
        .imageUrl("image")
        .authorEntity(authorEntity)
        .build();
  }

  public static AuthorEntity CreateArtistPunkAndRock() {
    return AuthorEntity.builder()
        .id(1L)
        .genres(Arrays.asList(MediaGenre.Punk, MediaGenre.Rock))
        .type(AuthorType.Artist)
        .name("nirvana")
        .build();
  }

  public static AuthorEntity CreateArtistExperimentalAndNoise() {
    return AuthorEntity.builder()
        .id(2L)
        .genres(Arrays.asList(MediaGenre.Experimental, MediaGenre.IndustrialAndNoise))
        .type(AuthorType.Artist)
        .name("swans")
        .build();
  }

  public static AuthorEntity CreateDirector() {
    return AuthorEntity.builder().id(2L).type(AuthorType.Director).name("Victor").build();
  }

  public static AuthorEntity CreateAuthor() {
    return AuthorEntity.builder().id(2L).type(AuthorType.Author).name("Victor").build();
  }

  public static UserEntity CreateUserA() {
    return UserEntity.builder()
        .id(1L)
        .email("Mark@example.com")
        .username("Mark")
        .password("12345678")
        .build();
  }

  public static UserEntity CreateUserB() {
    return UserEntity.builder()
        .id(2L)
        .email("Abe@example.com")
        .username("Abe")
        .password("12345678")
        .build();
  }

  public static String obtainAdminAccessToken(AuthService authService) {
    UserEntity request =
        UserEntity.builder()
            .email("admin@email.com")
            .password("adminpassowrd")
            .username("spacemen3")
            .role(UserRole.Admin)
            .build();
    AuthResponse authResponse = authService.register(request);

    return authResponse.getToken();
  }

  public static String obtainStandardAccessToken(AuthService authService){
    UserEntity request =
        UserEntity.builder()
            .email("admin@email.com")
            .password("adminpassowrd")
            .username("spacemen3")
            .role(UserRole.Standard)
            .build();
    AuthResponse authResponse = authService.register(request);

    return authResponse.getToken();
  }
}
