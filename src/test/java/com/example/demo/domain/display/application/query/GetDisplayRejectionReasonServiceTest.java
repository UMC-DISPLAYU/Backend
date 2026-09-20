package com.example.demo.domain.display.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.application.result.DisplayRejectionReasonResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayScreening;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.repository.DisplayScreeningRepository;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class GetDisplayRejectionReasonServiceTest {

  private static final Long DISPLAY_ID = 1L;
  private static final Long USER_ID = 2L;

  private final DisplayRepository displayRepository = mock(DisplayRepository.class);
  private final DisplayScreeningRepository screeningRepository =
      mock(DisplayScreeningRepository.class);
  private final GetDisplayRejectionReasonService service =
      new GetDisplayRejectionReasonService(
          displayRepository, screeningRepository, new DisplayPermissionChecker());

  @Test
  void returnsLatestRejectionReasonForAcceptedMember() {
    Display display = rejectedDisplay();
    DisplayScreening screening = mock(DisplayScreening.class);
    when(screening.getRejectionReason()).thenReturn("일정과 장소를 보완해주세요.");
    when(screeningRepository.findLatestByDisplayId(DISPLAY_ID)).thenReturn(Optional.of(screening));

    DisplayRejectionReasonResult result = service.getRejectionReason(DISPLAY_ID, USER_ID);

    assertThat(result.displayId()).isEqualTo(DISPLAY_ID);
    assertThat(result.publishStatus()).isEqualTo(DisplayStatus.REJECTED);
    assertThat(result.rejectionReason()).isEqualTo("일정과 장소를 보완해주세요.");
  }

  @Test
  void rejectsUserWhoIsNotAnAcceptedMember() {
    Display display = mock(Display.class);
    when(displayRepository.findById(DISPLAY_ID)).thenReturn(Optional.of(display));

    assertError(GlobalErrorCode.FORBIDDEN);
    verifyNoInteractions(screeningRepository);
  }

  @ParameterizedTest
  @EnumSource(value = DisplayStatus.class, names = "REJECTED", mode = EnumSource.Mode.EXCLUDE)
  void rejectsDisplayThatIsNotCurrentlyRejected(DisplayStatus status) {
    Display display = mock(Display.class);
    when(displayRepository.findById(DISPLAY_ID)).thenReturn(Optional.of(display));
    when(display.hasAcceptedTeamMember(USER_ID)).thenReturn(true);
    when(display.getStatus()).thenReturn(status);

    assertError(DisplayErrorCode.DISPLAY_REJECTION_REASON_UNAVAILABLE);
    verifyNoInteractions(screeningRepository);
  }

  @Test
  void rejectsMissingDisplay() {
    when(displayRepository.findById(DISPLAY_ID)).thenReturn(Optional.empty());

    assertError(DisplayErrorCode.DISPLAY_NOT_FOUND);
    verifyNoInteractions(screeningRepository);
  }

  @Test
  void rejectsDeletedDisplay() {
    Display display = mock(Display.class);
    when(display.isDeleted()).thenReturn(true);
    when(displayRepository.findById(DISPLAY_ID)).thenReturn(Optional.of(display));

    assertError(DisplayErrorCode.DISPLAY_NOT_FOUND);
    verifyNoInteractions(screeningRepository);
  }

  @Test
  void rejectsRejectedDisplayWithoutScreeningHistory() {
    rejectedDisplay();
    when(screeningRepository.findLatestByDisplayId(DISPLAY_ID)).thenReturn(Optional.empty());

    assertError(DisplayErrorCode.DISPLAY_SCREENING_NOT_FOUND);
  }

  private Display rejectedDisplay() {
    Display display = mock(Display.class);
    when(displayRepository.findById(DISPLAY_ID)).thenReturn(Optional.of(display));
    when(display.hasAcceptedTeamMember(USER_ID)).thenReturn(true);
    when(display.getId()).thenReturn(DISPLAY_ID);
    when(display.getStatus()).thenReturn(DisplayStatus.REJECTED);
    return display;
  }

  private void assertError(com.example.demo.global.error.BaseErrorCode errorCode) {
    assertThatThrownBy(() -> service.getRejectionReason(DISPLAY_ID, USER_ID))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception -> assertThat(exception.errorCode()).isEqualTo(errorCode));
  }
}
