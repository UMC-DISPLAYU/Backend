package com.example.demo.domain.displayartwork.application.command;

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
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.test.util.ReflectionTestUtils;

/** 작품 알림 테스트에서 쓰는 전시·작품 픽스처. 대표자(1), 팀원(2), 다른 팀원(3)이 있는 전시 "FORM 2026". */
final class DisplayArtworkNotificationFixtures {

  static final Long LEADER = 1L;
  static final Long MEMBER = 2L;
  static final Long OTHER_MEMBER = 3L;
  static final String DISPLAY_TITLE = "FORM 2026";

  private DisplayArtworkNotificationFixtures() {}

  static Display display() {
    Display display =
        Display.create(
            new UserId(LEADER),
            DISPLAY_TITLE,
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
        new TeamMember(12L, new UserId(MEMBER), "팀원", TeamMemberRole.TEAM_MEM, true));
    display.addTeamMember(
        new TeamMember(13L, new UserId(OTHER_MEMBER), "다른 팀원", TeamMemberRole.TEAM_MEM, true));
    return display;
  }

  static DisplayArtwork artwork(Display display, Long id, String name, Long registeredByUserId) {
    DisplayArtwork artwork =
        DisplayArtwork.create(
            display,
            name,
            "작품 설명입니다.",
            List.of(ArtworkType.GRAPHIC),
            2026,
            "캔버스",
            "100x100",
            "감상 포인트",
            0,
            registeredByUserId,
            List.of(
                new ArtworkImage(
                    null,
                    "https://cdn.displayu.com/artwork/main.jpg",
                    true,
                    ArtworkImageType.ARTWORK,
                    0,
                    null,
                    1200,
                    800)));
    ReflectionTestUtils.setField(artwork, "id", id);
    return artwork;
  }
}
