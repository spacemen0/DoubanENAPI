package com.soma.doubanen.domains.entities;

import com.soma.doubanen.domains.enums.MediaType;
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
@Table(
    name = "reviews",
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

  private Long likes;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "review_liked_users",
      joinColumns = @JoinColumn(name = "review_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private List<UserEntity> likedUsers;

  @Enumerated(EnumType.STRING)
  private MediaType type;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "user_id")
  private UserEntity user;
}
