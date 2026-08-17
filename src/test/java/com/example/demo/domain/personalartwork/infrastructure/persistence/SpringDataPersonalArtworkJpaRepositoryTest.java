package com.example.demo.domain.personalartwork.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.entity.PersonalArtworkImage;
import com.example.demo.domain.personalartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.personalartwork.domain.type.ArtworkType;
import com.example.demo.domain.personalartwork.domain.vo.UserId;
import com.example.demo.global.config.JpaAuditingConfig;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * 이 조회 결과는 archive 도메인이 "작품이 존재하는가"를 판단하는 근거로 쓰인다. PersonalArtwork는 소프트 삭제라 삭제 후에도 row가 남으므로, 삭제분이
 * 결과에 섞이면 삭제된 작품이 저장 목록에 노출되고 저장까지 통과한다.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class SpringDataPersonalArtworkJpaRepositoryTest {

  private static final Long OWNER = 1L;
  private static final Long ANOTHER_OWNER = 2L;

  @Autowired private SpringDataPersonalArtworkJpaRepository jpaRepository;

  @Autowired private EntityManager entityManager;

  @Test
  void findAllByIdInAndDeletedAtIsNullExcludesDeletedArtwork() {
    PersonalArtwork living = jpaRepository.saveAndFlush(artwork(OWNER, "살아있는 작품"));
    PersonalArtwork deleted = jpaRepository.saveAndFlush(artwork(OWNER, "삭제된 작품"));
    deleted.delete();
    jpaRepository.saveAndFlush(deleted);

    List<PersonalArtwork> found =
        jpaRepository.findAllByIdInAndDeletedAtIsNull(List.of(living.getId(), deleted.getId()));

    assertThat(found).extracting(PersonalArtwork::getId).containsExactly(living.getId());
  }

  @Test
  void findAllByIdInAndDeletedAtIsNullReturnsEmptyWhenAllArtworksAreDeleted() {
    PersonalArtwork deleted = jpaRepository.saveAndFlush(artwork(OWNER, "삭제된 작품"));
    deleted.delete();
    jpaRepository.saveAndFlush(deleted);

    // archive 도메인이 "결과가 비어있으면 존재하지 않는다"로 판단하므로 빈 결과여야 저장이 차단된다.
    assertThat(jpaRepository.findAllByIdInAndDeletedAtIsNull(List.of(deleted.getId()))).isEmpty();
  }

  @Test
  void findAllByIdInAndDeletedAtIsNullReturnsArtworksAcrossDifferentOwners() {
    PersonalArtwork mine = jpaRepository.saveAndFlush(artwork(OWNER, "내 작품"));
    PersonalArtwork others = jpaRepository.saveAndFlush(artwork(ANOTHER_OWNER, "다른 사람 작품"));

    // 소유자 기준 조회와 달리, 저장해 둔 ID들의 소유자가 서로 달라도 한 번에 조회돼야 한다.
    List<PersonalArtwork> found =
        jpaRepository.findAllByIdInAndDeletedAtIsNull(List.of(mine.getId(), others.getId()));

    assertThat(found)
        .extracting(PersonalArtwork::getId)
        .containsExactlyInAnyOrder(mine.getId(), others.getId());
  }

  @Test
  void keepsExistingFieldRowWhenArtworkIsUpdatedWithSameField() {
    PersonalArtwork saved =
        jpaRepository.saveAndFlush(artwork(OWNER, "작품", List.of(ArtworkType.COMPLEX)));
    Long fieldRowId = saved.getFields().getFirst().getId();
    entityManager.clear();

    PersonalArtwork loaded = jpaRepository.findById(saved.getId()).orElseThrow();
    loaded.changeContent("수정된 작품", "수정된 설명", List.of(ArtworkType.COMPLEX), 2026, "재료", "크기", "포인트");
    jpaRepository.saveAndFlush(loaded);
    entityManager.clear();

    PersonalArtwork reloaded = jpaRepository.findById(saved.getId()).orElseThrow();
    assertThat(reloaded.getFieldTypes()).containsExactly(ArtworkType.COMPLEX);
    // 유지되는 분야는 기존 행을 그대로 둔다. 지우고 다시 넣으면 유니크 제약에 걸려 저장 자체가 실패한다.
    assertThat(reloaded.getFields().getFirst().getId()).isEqualTo(fieldRowId);
  }

  @Test
  void replacesOnlyChangedFieldWhenArtworkIsUpdated() {
    PersonalArtwork saved =
        jpaRepository.saveAndFlush(
            artwork(OWNER, "작품", List.of(ArtworkType.COMPLEX, ArtworkType.PAINTING)));
    Long keptRowId =
        saved.getFields().stream()
            .filter(field -> field.getField() == ArtworkType.COMPLEX)
            .findFirst()
            .orElseThrow()
            .getId();
    entityManager.clear();

    PersonalArtwork loaded = jpaRepository.findById(saved.getId()).orElseThrow();
    loaded.changeContent(
        "수정된 작품", "설명", List.of(ArtworkType.COMPLEX, ArtworkType.MEDIA), 2026, "재료", "크기", "포인트");
    jpaRepository.saveAndFlush(loaded);
    entityManager.clear();

    PersonalArtwork reloaded = jpaRepository.findById(saved.getId()).orElseThrow();
    assertThat(reloaded.getFieldTypes())
        .containsExactlyInAnyOrder(ArtworkType.COMPLEX, ArtworkType.MEDIA);
    assertThat(
            reloaded.getFields().stream()
                .filter(field -> field.getField() == ArtworkType.COMPLEX)
                .findFirst()
                .orElseThrow()
                .getId())
        .isEqualTo(keptRowId);
  }

  private static PersonalArtwork artwork(Long ownerUserId, String artworkName) {
    return artwork(ownerUserId, artworkName, List.of(ArtworkType.COMPLEX));
  }

  private static PersonalArtwork artwork(
      Long ownerUserId, String artworkName, List<ArtworkType> types) {
    return PersonalArtwork.create(
        new UserId(ownerUserId),
        artworkName,
        "content",
        types,
        2026,
        "Mixed media",
        "100 x 100 cm",
        "point",
        List.of(
            new PersonalArtworkImage(
                null,
                "https://cdn.displayu.com/personal-artworks/%s.png".formatted(artworkName),
                true,
                ArtworkImageType.ARTWORK,
                1,
                "대표 이미지",
                1200,
                1600)));
  }
}
