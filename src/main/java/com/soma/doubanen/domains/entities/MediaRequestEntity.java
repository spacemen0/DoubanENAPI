package com.soma.doubanen.domains.entities;

import com.soma.doubanen.domains.enums.MediaGenre;
import com.soma.doubanen.domains.enums.MediaType;
import com.soma.doubanen.domains.enums.RequestStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "media_requests")
public class MediaRequestEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_request_id_seq")
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

  @FullTextField private String author_name;

  @ManyToOne()
  @JoinColumn(name = "author_id")
  private AuthorEntity authorEntity;

  @Column(nullable = false)
  private Long userId;

  private Long resourceId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RequestStatus status;

  private LocalDateTime actionTime;

  private String message;
}
