package com.soma.doubanen.domains.entities;

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
    name = "review",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"media_id", "user_id"})})
public class ReviewEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_id_seq")
  private Long id;

  @Column(nullable = false)
  private Float score;

  private LocalDate date;

  @Column(nullable = false)
  private Long mediaId;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "user_id")
  private UserEntity user;
}
