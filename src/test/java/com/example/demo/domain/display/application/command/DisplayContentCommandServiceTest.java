package com.example.demo.domain.display.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.application.result.DisplayContentResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayContent;
import com.example.demo.domain.display.domain.entity.DisplayContentCategory;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayContentStatus;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

class DisplayContentCommandServiceTest {

  private final DisplayRepository displayRepository = mock(DisplayRepository.class);
  private final Clock clock =
      Clock.fixed(Instant.parse("2026-08-01T15:00:00Z"), ZoneId.of("Asia/Seoul"));
  private final DisplayContentCommandService service =
      new DisplayContentCommandService(displayRepository, clock, new DisplayPermissionChecker());

  @Test
  void createContentCreatesDraftWhenDisplayIsDraftEvenAfterDisplayStarted() {
    Display display = display();
    display.addTeamMember(new TeamMember(1L, new UserId(2L), "팀원", TeamMemberRole.TEAM_MEM, true));
    DisplayContentCategory category =
        new DisplayContentCategory(1L, "전시 소개", "전시 소개 이미지입니다.", 0, List.of());
    display.addContentCategory(category);
    when(displayRepository.findById(1L)).thenReturn(Optional.of(display));

    service.createContent(
        new CreateDisplayContentCommand(
            2L, 1L, 1L, "https://cdn.displayu.com/display/content.jpg"));

    assertThat(category.getContents().getFirst().getStatus()).isEqualTo(DisplayContentStatus.DRAFT);
    assertThat(category.getContents().getFirst().getUserId().value()).isEqualTo(2L);
  }

  @Test
  void updateContentKeepsOriginalUploaderUserId() {
    Display display = display();
    display.addTeamMember(new TeamMember(1L, new UserId(2L), "팀원", TeamMemberRole.TEAM_MEM, true));
    display.addContentCategory(
        new DisplayContentCategory(
            1L,
            "전시장 전경",
            "전시장 이미지입니다.",
            0,
            List.of(
                new DisplayContent(
                    1L,
                    "https://cdn.displayu.com/display/content-1.jpg",
                    0,
                    DisplayContentStatus.PUBLISHED,
                    new UserId(2L)))));
    when(displayRepository.findById(1L)).thenReturn(Optional.of(display));

    DisplayContentResult result =
        service.updateContent(
            new UpdateDisplayContentCommand(
                2L, 1L, 1L, 1L, "https://cdn.displayu.com/display/content-updated.jpg"));

    assertThat(result.userId()).isEqualTo(2L);
    assertThat(result.imageUrl()).isEqualTo("https://cdn.displayu.com/display/content-updated.jpg");
  }

  @Test
  void reorderContentsTranslatesOptimisticLockConflictToBusinessException() {
    Display display = displayWithThreeContents();
    when(displayRepository.findByIdWithOptimisticLock(1L)).thenReturn(Optional.of(display));
    doThrow(new ObjectOptimisticLockingFailureException(Display.class, 1L))
        .when(displayRepository)
        .flush();

    assertThatThrownBy(
            () ->
                service.reorderContents(
                    new ReorderDisplayContentsCommand(2L, 1L, 1L, List.of(3L, 1L, 2L))))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayErrorCode.DISPLAY_CONTENT_REORDER_CONFLICT));
  }

  private static Display displayWithThreeContents() {
    Display display = display();
    display.addTeamMember(new TeamMember(1L, new UserId(2L), "팀원", TeamMemberRole.TEAM_MEM, true));
    display.addContentCategory(
        new DisplayContentCategory(
            1L,
            "전시장 전경",
            "전시장 이미지입니다.",
            0,
            List.of(
                new DisplayContent(1L, "https://cdn.displayu.com/display/content-1.jpg", 0),
                new DisplayContent(2L, "https://cdn.displayu.com/display/content-2.jpg", 1),
                new DisplayContent(3L, "https://cdn.displayu.com/display/content-3.jpg", 2))));
    return display;
  }

  private static Display display() {
    return Display.create(
        new UserId(1L),
        "FORM 2026",
        "https://cdn.displayu.com/posters/main.png",
        "subtitle",
        "content",
        new DisplayLocation("전시장", bd("37.5513"), bd("126.9248")),
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
        ContentOpenPolicy.ON_EXHIBITION);
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}
