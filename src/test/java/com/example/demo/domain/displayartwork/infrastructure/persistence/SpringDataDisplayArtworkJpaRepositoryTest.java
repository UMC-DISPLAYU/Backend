package com.example.demo.domain.displayartwork.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.domain.displayartwork.domain.type.CreatorRole;
import com.example.demo.global.config.JpaAuditingConfig;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * 작가 프로필의 작품 탭이 쓰는 조회다. Creator는 작가(LEAD_ARTIST/CO_AUTHOR) 외에 QnA 답변만 담당하는 전시 대표자(QA_ONLY)로도 생성되므로,
 * role을 구분하지 않으면 대리 등록해 준 남의 작품이 대표자의 작가 프로필에 노출된다.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class SpringDataDisplayArtworkJpaRepositoryTest {

  private static final Long DISPLAY_OWNER = 1L;
  private static final Long PARTICIPANT = 10L;

  @Autowired private SpringDataDisplayArtworkJpaRepository jpaRepository;

  @Autowired private EntityManager entityManager;

  private long nextCreatorId = 1L;

  @Test
  void findAllByParticipantUserIdIncludesLeadArtist() {
    Display display = persistedDisplay();
    DisplayArtwork artwork = persistArtwork(display, "대표 작가로 참여한 작품", 0);
    persistCreator(artwork.getId(), "참여 작가", CreatorRole.LEAD_ARTIST, PARTICIPANT);

    assertThat(jpaRepository.findAllByParticipantUserId(PARTICIPANT))
        .extracting(DisplayArtwork::getId)
        .containsExactly(artwork.getId());
  }

  @Test
  void findAllByParticipantUserIdIncludesCoAuthor() {
    Display display = persistedDisplay();
    DisplayArtwork artwork = persistArtwork(display, "공동 작업자로 참여한 작품", 0);
    persistCreator(artwork.getId(), "대표 작가", CreatorRole.LEAD_ARTIST, null);
    persistCreator(artwork.getId(), "참여 작가", CreatorRole.CO_AUTHOR, PARTICIPANT);

    assertThat(jpaRepository.findAllByParticipantUserId(PARTICIPANT))
        .extracting(DisplayArtwork::getId)
        .containsExactly(artwork.getId());
  }

  @Test
  void findAllByParticipantUserIdExcludesQaOnlyHandler() {
    Display display = persistedDisplay();
    DisplayArtwork artwork = persistArtwork(display, "대리 등록해 준 남의 작품", 0);
    // 계정 없는 작가를 대리 등록하면 대표 작가의 userId는 null이고, 담당자 지정을 위해 대표자에게 QA_ONLY가 생긴다.
    persistCreator(artwork.getId(), "계정 없는 작가", CreatorRole.LEAD_ARTIST, null);
    persistCreator(artwork.getId(), "QnA 담당자", CreatorRole.QA_ONLY, PARTICIPANT);

    assertThat(jpaRepository.findAllByParticipantUserId(PARTICIPANT)).isEmpty();
  }

  @Test
  void findAllByParticipantUserIdReturnsOnlyArtworksParticipatedAsArtist() {
    Display display = persistedDisplay();
    DisplayArtwork own = persistArtwork(display, "본인이 작가인 작품", 0);
    DisplayArtwork proxied = persistArtwork(display, "대리 등록만 해 준 작품", 1);
    persistCreator(own.getId(), "참여 작가", CreatorRole.LEAD_ARTIST, PARTICIPANT);
    persistCreator(proxied.getId(), "계정 없는 작가", CreatorRole.LEAD_ARTIST, null);
    persistCreator(proxied.getId(), "QnA 담당자", CreatorRole.QA_ONLY, PARTICIPANT);

    assertThat(jpaRepository.findAllByParticipantUserId(PARTICIPANT))
        .extracting(DisplayArtwork::getId)
        .containsExactly(own.getId());
  }

  private Display persistedDisplay() {
    Display display =
        Display.create(
            new UserId(DISPLAY_OWNER),
            "FORM 2026",
            "https://cdn.displayu.com/posters/main.png",
            "subtitle",
            "content",
            new DisplayLocation("전시장", new BigDecimal("37.5513"), new BigDecimal("126.9248")),
            "",
            "",
            "organization",
            "department",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            DisplayRegion.SEOUL,
            new DisplayPeriod(
                LocalDate.of(2026, 5, 28),
                LocalDate.of(2026, 6, 5),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.IMMEDIATELY);
    // 조회 조건이 전시/작품 모두 PUBLISHED를 요구한다.
    display.publish();
    entityManager.persist(display);
    entityManager.flush();
    return display;
  }

  private DisplayArtwork persistArtwork(Display display, String artworkName, int workSortOrder) {
    DisplayArtwork artwork =
        DisplayArtwork.create(
            display,
            artworkName,
            "content",
            ArtworkType.PAINTING,
            2026,
            "Oil on canvas",
            "72.7 x 90.9 cm",
            "point",
            workSortOrder,
            DISPLAY_OWNER,
            List.of(
                new ArtworkImage(
                    null,
                    "https://cdn.displayu.com/artworks/main.png",
                    true,
                    ArtworkImageType.ARTWORK,
                    0,
                    "대표 이미지",
                    1200,
                    1600)));
    entityManager.persist(artwork);
    entityManager.flush();
    return artwork;
  }

  /**
   * 계정 없는 작가를 대리 등록하는 경우를 재현해야 하므로 userId에 null을 받는다.
   *
   * <p>Creator 테이블은 artworkcommunication의 참조 엔티티도 매핑하는데 그쪽에 식별자 생성 전략이 없어, H2 스키마의 creator_id에
   * identity가 붙지 않는다. 그래서 JPA persist 대신 ID를 직접 지정해 넣는다. 운영 MySQL은 Flyway가 auto_increment로 만들므로 영향이
   * 없다.
   */
  private void persistCreator(Long artworkId, String creatorName, CreatorRole role, Long userId) {
    entityManager
        .createNativeQuery(
            """
            INSERT INTO creator
              (creator_id, display_artwork_id, creator_name, is_contact, is_leader, role, user_id)
            VALUES (?, ?, ?, FALSE, ?, ?, ?)
            """)
        .setParameter(1, nextCreatorId++)
        .setParameter(2, artworkId)
        .setParameter(3, creatorName)
        .setParameter(4, role == CreatorRole.LEAD_ARTIST)
        .setParameter(5, role.name())
        .setParameter(6, userId)
        .executeUpdate();
    entityManager.flush();
    entityManager.clear();
  }
}
