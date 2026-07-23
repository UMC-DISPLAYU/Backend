package com.example.demo.domain.display.domain.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
}
