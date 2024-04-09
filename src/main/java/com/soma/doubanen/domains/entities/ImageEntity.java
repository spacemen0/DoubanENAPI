package com.soma.doubanen.domains.entities;

import com.soma.doubanen.domains.enums.ImageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Images")
public class ImageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Column(nullable = false)
  private Long objectId;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ImageType type;

  @Lob
  @Column(nullable = false)
  private byte[] imageData;
}
