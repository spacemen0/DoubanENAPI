package com.soma.doubanen.domains.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "media_lists")
public class MediaListEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_list_id_seq")
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  private LocalDate date;

  @ManyToMany(fetch = FetchType.EAGER)
  private List<MediaEntity> mediaEntities;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "user_id")
  private UserEntity userEntity;
}
