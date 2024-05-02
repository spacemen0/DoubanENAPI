package com.soma.doubanen.domains.entities;

import com.soma.doubanen.domains.enums.CommentArea;
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
@Table(name = "comments")
public class CommentEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_status_id_seq")
  private Long id;

  private LocalDate date;

  @Enumerated(EnumType.STRING)
  private CommentArea commentArea;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(nullable = false)
  private Long areaId;

  @Column(nullable = false)
  private Long userId;
}
