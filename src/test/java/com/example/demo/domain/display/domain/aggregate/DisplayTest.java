package com.example.demo.domain.display.domain.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.display.domain.entity.DisplayContent;
import com.example.demo.domain.display.domain.entity.DisplayContentCategory;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayInvitationStatus;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.global.error.BusinessException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class DisplayTest {

  @Test
  void createContentAssignsNextSortOrder() {
    Display display = display();
    DisplayContentCategory category =
        new DisplayContentCategory(
            1L,
            "전시장 전경",
            "전시장 이미지",
            0,
            List.of(new DisplayContent(1L, "https://cdn.displayu.com/1.jpg", 100, 100, 0)));
    display.addContentCategory(category);

    DisplayContent content = display.createContent(1L, "https://cdn.displayu.com/2.jpg", 100, 100);

    assertThat(content.getSortOrder()).isEqualTo(1);
  }

  @Test
  void createContentFailsWhenCategoryAlreadyHasTwentyContents() {
    Display display = display();
    DisplayContentCategory category =
        new DisplayContentCategory(1L, "전시장 전경", "전시장 이미지", 0, twentyContents());
    display.addContentCategory(category);

    assertThatThrownBy(() -> display.createContent(1L, "https://cdn.displayu.com/21.jpg", 100, 100))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayErrorCode.DISPLAY_CONTENT_LIMIT_EXCEEDED));
  }

  @Test
  void createContentFailsWhenCategoryDoesNotExist() {
    Display display = display();

    assertThatThrownBy(
            () -> display.createContent(999L, "https://cdn.displayu.com/1.jpg", 100, 100))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayErrorCode.DISPLAY_CONTENT_CATEGORY_NOT_FOUND));
  }

  @Test
  void reorderContentsChangesSortOrderByRequestedOrder() {
    Display display = display();
    DisplayContentCategory category =
        new DisplayContentCategory(
            1L,
            "전시장 전경",
            "전시장 이미지",
            0,
            List.of(
                new DisplayContent(1L, "https://cdn.displayu.com/1.jpg", 100, 100, 0),
                new DisplayContent(2L, "https://cdn.displayu.com/2.jpg", 100, 100, 1),
                new DisplayContent(3L, "https://cdn.displayu.com/3.jpg", 100, 100, 2)));
    display.addContentCategory(category);

    display.reorderContents(1L, List.of(3L, 1L, 2L));

    assertThat(category.findContent(3L).getSortOrder()).isZero();
    assertThat(category.findContent(1L).getSortOrder()).isEqualTo(1);
    assertThat(category.findContent(2L).getSortOrder()).isEqualTo(2);
  }

  @Test
  void reorderContentsFailsWhenRequestedIdsDoNotMatchCurrentContents() {
    Display display = display();
    DisplayContentCategory category =
        new DisplayContentCategory(
            1L,
            "전시장 전경",
            "전시장 이미지",
            0,
            List.of(
                new DisplayContent(1L, "https://cdn.displayu.com/1.jpg", 100, 100, 0),
                new DisplayContent(2L, "https://cdn.displayu.com/2.jpg", 100, 100, 1)));
    display.addContentCategory(category);

    assertThatThrownBy(() -> display.reorderContents(1L, List.of(1L, 1L)))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayErrorCode.INVALID_DISPLAY_CONTENT_ORDER));
  }

  @Test
  void inviteeAsTeamMemberFailsWhenInvitationIsNotAccepted() {
    Display display = display();
    DisplayInvitation invitation =
        new DisplayInvitation(
            null,
            new UserId(1L),
            new UserId(2L),
            DisplayInvitationStatus.PENDING,
            LocalDateTime.now(),
            null,
            null);
    display.addInvitation(invitation);

    assertThatThrownBy(() -> display.inviteeAsTeamMember(invitation, "member"))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayErrorCode.INVALID_DISPLAY_INVITATION_STATUS));
  }

  @Test
  void inviteeAsTeamMemberFailsWhenInvitationIsDeleted() {
    Display display = display();
    DisplayInvitation invitation =
        new DisplayInvitation(
            null,
            new UserId(1L),
            new UserId(2L),
            DisplayInvitationStatus.ACCEPTED,
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now());
    display.addInvitation(invitation);

    assertThatThrownBy(() -> display.inviteeAsTeamMember(invitation, "member"))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayErrorCode.DISPLAY_INVITATION_ALREADY_REJECTED));
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

  private static List<DisplayContent> twentyContents() {
    return java.util.stream.IntStream.range(0, 20)
        .mapToObj(
            index ->
                new DisplayContent(
                    (long) index + 1,
                    "https://cdn.displayu.com/" + (index + 1) + ".jpg",
                    100,
                    100,
                    index))
        .toList();
  }
}
