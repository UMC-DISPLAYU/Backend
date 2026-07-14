package com.example.demo.domain.displayartwork.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@Entity
@Table(name = "DisplayArtworkLike")
public class DisplayArtworkLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "displayArtworkLikeId")
  private Long id;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private Long displayArtworkId;

  @Column(nullable = false)
  private Long userId;

  protected DisplayArtworkLike() {}

  private DisplayArtworkLike(Long displayArtworkId, Long userId) {
    this.displayArtworkId = displayArtworkId;
    this.userId = userId;
  }

  public static DisplayArtworkLike create(Long displayArtworkId, Long userId) {
    return new DisplayArtworkLike(displayArtworkId, userId);
  }
}
