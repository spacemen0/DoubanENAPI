package com.soma.doubanen.domains.entities;

import com.soma.doubanen.domains.enums.MediaGenre;
import com.soma.doubanen.domains.enums.MediaType;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "medias")
public class MediaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_id_seq")
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(columnDefinition = "TEXT")
  private String additionalInfo;

  @Column(nullable = false)
  private LocalDate releaseDate;

  @Column(nullable = false)
  private Float average;

  @Column(nullable = false)
  private Long ratings;

  @Column(nullable = false)
  private Long wants;

  @Column(nullable = false)
  private Long doings;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MediaGenre genre;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MediaType type;

  @Column(nullable = false)
  private String imageUrl;

  @ManyToOne()
  @JoinColumn(name = "author_id")
  private AuthorEntity authorEntity;

  @Override
  public String toString() {
    return "MediaEntity{"
        + "id="
        + id
        + ", title='"
        + title
        + '\''
        + ", description='"
        + description
        + '\''
        + ", additionalInfo='"
        + additionalInfo
        + '\''
        + ", releaseDate='"
        + releaseDate
        + '\''
        + ", average="
        + average
        + ", ratings="
        + ratings
        + ", wants="
        + wants
        + ", genre="
        + genre
        + ", type="
        + type
        + ", imageUrl='"
        + imageUrl
        + '\''
        + ", authorEntity="
        + (authorEntity != null ? authorEntity.getId() : null)
        + '}';
  }
}
