package com.soma.doubanen.domains.entities;

import com.soma.doubanen.domains.enums.AuthorType;
import com.soma.doubanen.domains.enums.MediaGenre;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "authors")
@EqualsAndHashCode
public class AuthorEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "author_id_seq")
  private Long id;

  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AuthorType type;

  @EqualsAndHashCode.Exclude
  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  @CollectionTable(name = "media_genres", joinColumns = @JoinColumn(name = "author_id"))
  @Column(name = "genre")
  private List<MediaGenre> genres;

  @OneToMany(
      cascade = {CascadeType.DETACH},
      mappedBy = "authorEntity",
      fetch = FetchType.EAGER)
  private List<MediaEntity> mediaEntities;
}
