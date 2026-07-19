package com.example.demo.domain.displayartwork.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "Creator")
public class Creator {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "creatorId")
  private Long id;

  @Column(nullable = false)
  private String creatorName;

  @Column(nullable = false)
  private boolean isContact;

  @Column(nullable = false)
  private boolean isLeader;

  private Long userId;

  @Column(nullable = false)
  private Long displayArtworkId;

  protected Creator() {}

  public Creator(
      Long id,
      String creatorName,
      boolean isContact,
      boolean isLeader,
      Long userId,
      Long displayArtworkId) {
    this.id = id;
    this.creatorName = requireNonBlank(creatorName, "creatorName");
    this.isContact = isContact;
    this.isLeader = isLeader;
    this.userId = userId;
    this.displayArtworkId = displayArtworkId;
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank.");
    }
    return value;
  }
}
