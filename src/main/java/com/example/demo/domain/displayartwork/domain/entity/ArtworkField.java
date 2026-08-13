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
import java.util.Objects;
import lombok.Getter;

/**
 * 작품이 속한 분야. 한 작품은 최대 2개까지 가질 수 있다.
 *
 * <p>작가 프로필의 활동 분야({@code AreaOfActivity})와 같은 방식으로 별도 테이블에 둔다. 미리보기 필터가 "고른 분야를 모두 가진 작품"을 AND로 찾기
 * 때문에, 분야를 행으로 두어야 개수를 세어 판별할 수 있다.
 */
@Getter
@Entity
@Table(name = "ArtworkField")
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
