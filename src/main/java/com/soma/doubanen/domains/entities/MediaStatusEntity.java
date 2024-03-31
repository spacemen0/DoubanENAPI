package com.soma.doubanen.domains.entities;

import com.soma.doubanen.domains.enums.MediaStatus;
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
@Table(
    name = "media_statuses",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"media_id", "user_id"})})
public class MediaStatusEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_status_id_seq")
  private Long id;

  private Float score;

  private LocalDate date;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MediaType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MediaStatus status;

  @Column(nullable = false)
  private Long mediaId;

  @Column(nullable = false)
  private Long userId;
}
