package com.example.demo.domain.personalartwork.domain.entity;

import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.type.ArtworkType;
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
 * 개인 작품이 속한 분야. 한 작품은 최대 2개까지 가질 수 있다.
 *
 * <p>전시 출품작의 {@code ArtworkField}와 같은 구조다. 개인 작품에는 분야 필터 조회가 없어 단순 보관 용도지만, 두 도메인의 작품 표현을 맞추기 위해 같은
 * 방식으로 둔다.
 */
@Getter
@Entity
@Table(name = "PersonalArtworkField")
public class PersonalArtworkField {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalArtworkFieldId")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "personalArtworkId", nullable = false)
  private PersonalArtwork personalArtwork;

  @Enumerated(EnumType.STRING)
  @Column(name = "field", nullable = false)
  private ArtworkType field;

  protected PersonalArtworkField() {}

  public PersonalArtworkField(ArtworkType field) {
    this.field = Objects.requireNonNull(field, "field must not be null.");
  }

  /** 애그리거트가 컬렉션에 담을 때 역방향 참조를 채운다. */
  public void assignPersonalArtwork(PersonalArtwork personalArtwork) {
    this.personalArtwork =
        Objects.requireNonNull(personalArtwork, "personalArtwork must not be null.");
  }
}
