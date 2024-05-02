package com.soma.doubanen.domains.entities;

import com.soma.doubanen.domains.enums.AuthorType;
import com.soma.doubanen.domains.enums.MediaGenre;
import com.soma.doubanen.domains.enums.RequestStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.springframework.stereotype.Indexed;

@AllArgsConstructor
@Indexed
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "author_requests")
@EqualsAndHashCode
public class AuthorRequestEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "author_request_id_seq")
  private Long id;

  @FullTextField() private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AuthorType type;

  @EqualsAndHashCode.Exclude
  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  @CollectionTable(
      name = "media_genres_author_requests",
      joinColumns = @JoinColumn(name = "author_request_id"))
  @Column(name = "genre")
  private List<MediaGenre> genres;

  @Column(nullable = false)
  private Long userId;

  private Long resourceId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RequestStatus status;

  private LocalDateTime actionTime;

  private String message;
}
