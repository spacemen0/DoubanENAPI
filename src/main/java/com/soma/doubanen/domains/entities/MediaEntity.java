package com.soma.doubanen.domains.entities;

import com.soma.doubanen.domains.enums.MediaGenre;
import com.soma.doubanen.domains.enums.MediaType;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

@Indexed
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "media")
public class MediaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_id_seq")
  private Long id;

  @FullTextField()
  @Column(nullable = false)
  private String title;

  @FullTextField()
  @Column(columnDefinition = "TEXT")
  private String description;

  @FullTextField()
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
  @FullTextField()
  @Column(nullable = false)
  private MediaType type;

  @Column(nullable = false)
  private String imageUrl;

  @FullTextField private String author_name;

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
