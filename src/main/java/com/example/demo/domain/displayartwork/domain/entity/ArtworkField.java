package com.example.demo.domain.displayartwork.domain.entity;

import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(
    name = "ArtworkField",
    uniqueConstraints =
        @UniqueConstraint(
            name = "UQ_ARTWORKFIELD_ARTWORK_FIELD",
            columnNames = {"displayArtworkId", "field"}))
public class ArtworkField {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "artworkFieldId")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "displayArtworkId", nullable = false)
  private DisplayArtwork displayArtwork;

  @Enumerated(EnumType.STRING)
  @Column(name = "field", nullable = false)
  private ArtworkType field;

  protected ArtworkField() {}

  public ArtworkField(ArtworkType field) {
    this.field = Objects.requireNonNull(field, "field must not be null.");
  }

  /** 애그리거트가 컬렉션에 담을 때 역방향 참조를 채운다. */
  public void assignDisplayArtwork(DisplayArtwork displayArtwork) {
    this.displayArtwork =
        Objects.requireNonNull(displayArtwork, "displayArtwork must not be null.");
  }
}
