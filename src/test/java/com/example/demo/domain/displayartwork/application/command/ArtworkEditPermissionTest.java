package com.example.demo.domain.displayartwork.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.displayartwork.domain.entity.Creator;
import com.example.demo.domain.displayartwork.domain.repository.CreatorRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

/** PM이 확정한 전시작 수정·삭제 권한(대표자 + 작가 + 공동 작업자)을 검증한다. */
class ArtworkEditPermissionTest {

  private static final Long ARTWORK_ID = 10L;
  private static final Long LEADER = 1L;
  private static final Long ARTIST = 2L;
  private static final Long CO_AUTHOR = 3L;
  private static final Long OTHER_MEMBER = 4L;

  private final CreatorRepository creatorRepository = mock(CreatorRepository.class);
  private final ArtworkEditPermission permission = new ArtworkEditPermission(creatorRepository);

  @Test
  void 전시_대표자는_수정할_수_있다() {
    givenCreators(creator("작가", ARTIST, true));

    assertThat(permission.canEdit(display(), ARTWORK_ID, LEADER)).isTrue();
  }

  @Test
  void 작품의_작가는_수정할_수_있다() {
    givenCreators(creator("작가", ARTIST, true));

    assertThat(permission.canEdit(display(), ARTWORK_ID, ARTIST)).isTrue();
  }

  @Test
  void 공동_작업자로_추가된_팀원도_수정할_수_있다() {
    givenCreators(creator("작가", ARTIST, true), creator("공동작업자", CO_AUTHOR, false));

    assertThat(permission.canEdit(display(), ARTWORK_ID, CO_AUTHOR)).isTrue();
  }

  @Test
  void 작품과_무관한_팀원은_수정할_수_없다() {
    givenCreators(creator("작가", ARTIST, true));

    assertThat(permission.canEdit(display(), ARTWORK_ID, OTHER_MEMBER)).isFalse();
  }

  @Test
  void 팀원을_선택해_대리_등록한_작품은_선택된_팀원이_수정할_수_있다() {
    givenCreators(creator("대리 등록된 팀원", ARTIST, true));

    assertThat(permission.canEdit(display(), ARTWORK_ID, ARTIST)).isTrue();
  }

  @Test
  void 이름만_입력해_대리_등록한_작품은_대표자만_수정할_수_있다() {
    // 계정이 연결되지 않은 작가는 userId가 null이므로 대표자 외에는 매칭되지 않는다.
    givenCreators(creator("고상준", null, true));

    assertThat(permission.canEdit(display(), ARTWORK_ID, LEADER)).isTrue();
    assertThat(permission.canEdit(display(), ARTWORK_ID, ARTIST)).isFalse();
    assertThat(permission.canEdit(display(), ARTWORK_ID, OTHER_MEMBER)).isFalse();
  }

  @Test
  void 비로그인_요청은_수정할_수_없다() {
    assertThat(permission.canEdit(display(), ARTWORK_ID, null)).isFalse();
  }

  private void givenCreators(Creator... creators) {
    when(creatorRepository.findByDisplayArtworkId(ARTWORK_ID)).thenReturn(List.of(creators));
  }

  private static Creator creator(String name, Long userId, boolean isLeader) {
    return new Creator(null, name, false, isLeader, userId, ARTWORK_ID);
  }

  private static Display display() {
    Display display =
        Display.create(
            new UserId(LEADER),
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
    display.addTeamMember(
        new TeamMember(11L, new UserId(LEADER), "대표자", TeamMemberRole.TEAM_LEADER, true));
    display.addTeamMember(
        new TeamMember(12L, new UserId(ARTIST), "작가", TeamMemberRole.TEAM_MEM, true));
    display.addTeamMember(
        new TeamMember(13L, new UserId(CO_AUTHOR), "공동작업자", TeamMemberRole.TEAM_MEM, true));
    display.addTeamMember(
        new TeamMember(14L, new UserId(OTHER_MEMBER), "다른 팀원", TeamMemberRole.TEAM_MEM, true));
    return display;
  }
}
