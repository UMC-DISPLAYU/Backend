package com.example.demo.domain.notification.application.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.notification.domain.type.NotificationTargetType;
import com.example.demo.domain.notification.domain.type.NotificationType;
import com.example.demo.domain.notification.domain.vo.NotificationBodySegment;
import com.example.demo.domain.notification.domain.vo.NotificationTarget;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class NotificationMessagesTest {

  @Test
  void coAuthorAddedBoldsArtworkNameAndMovesToDisplayWorks() {
    NotificationMessage message = NotificationMessages.coAuthorAdded("접으면 달라지는 지도", 7L);

    assertThat(message.type()).isEqualTo(NotificationType.WORK_COAUTHOR_ADDED);
    assertThat(message.title()).isEqualTo("공동 작가로 추가됨");
    assertThat(text(message))
        .isEqualTo("“접으면 달라지는 지도” 작품에 공동 작가로 추가됐어요. 작가와 같은 권한으로 수정·삭제할 수 있어요.");
    assertThat(boldTexts(message)).containsExactly("접으면 달라지는 지도");
    assertThat(message.target())
        .isEqualTo(new NotificationTarget(NotificationTargetType.DISPLAY_WORKS, 7L));
  }

  @Test
  void proxyAssignedShowsRegistrantName() {
    NotificationMessage message = NotificationMessages.proxyAssigned("장면을 넘기는 극장", "김도윤", 7L);

    assertThat(text(message)).isEqualTo("“장면을 넘기는 극장” 작품의 작가로 지정됐어요. 등록한 사람: 김도윤");
    assertThat(boldTexts(message)).containsExactly("장면을 넘기는 극장", "김도윤");
  }

  @Test
  void artworkDeletedShowsDeleterAndDoesNotNavigate() {
    NotificationMessage message = NotificationMessages.artworkDeleted("얇은 경계", "서유진");

    assertThat(message.type()).isEqualTo(NotificationType.WORK_DELETED);
    assertThat(text(message)).isEqualTo("“얇은 경계” 작품이 삭제됐어요. 삭제한 사람: 서유진. 복구할 수 없어요.");
    assertThat(message.target()).isNull();
  }

  @Test
  void artworksDeletedByArtistLeaveWithOneArtworkShowsOnlyItsName() {
    NotificationMessage message =
        NotificationMessages.artworksDeletedByArtistLeave(List.of("얇은 경계"), "손끝의 지도");

    assertThat(text(message))
        .isEqualTo(
            "“얇은 경계” 작품이 삭제됐어요. 함께 작가로 등록된 계정이 “손끝의 지도” 전시에서 나가거나 내보내져 작품도 함께 삭제됐어요. 복구할 수"
                + " 없어요.");
    assertThat(boldTexts(message)).containsExactly("얇은 경계", "손끝의 지도");
  }

  @Test
  void artworksDeletedByArtistLeaveWithManyArtworksListsAllNamesWithCount() {
    NotificationMessage message =
        NotificationMessages.artworksDeletedByArtistLeave(
            List.of("얇은 경계", "되감는 계단", "비가 지난 가방"), "손끝의 지도");

    assertThat(message.type()).isEqualTo(NotificationType.WORK_DELETED_BY_ARTIST_LEAVE);
    assertThat(text(message))
        .isEqualTo(
            "작품 3점(“얇은 경계”, “되감는 계단”, “비가 지난 가방”)이 삭제됐어요. 함께 작가로 등록된 계정이 “손끝의 지도” 전시에서"
                + " 나가거나 내보내져 작품도 함께 삭제됐어요. 복구할 수 없어요.");
    assertThat(boldTexts(message)).containsExactly("3점", "얇은 경계", "되감는 계단", "비가 지난 가방", "손끝의 지도");
  }

  @Test
  void artworksDeletedByArtistLeaveRejectsEmptyArtworks() {
    assertThatThrownBy(() -> NotificationMessages.artworksDeletedByArtistLeave(List.of(), "전시"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void requestApprovedMovesToMyDisplays() {
    NotificationMessage message = NotificationMessages.requestApproved("빛의 결 | 조용한 흐름");

    assertThat(text(message))
        .isEqualTo("“빛의 결 | 조용한 흐름” 전시의 등록 요청이 승인됐어요. 승인된 시점에 저장돼 있던 내용이 공개돼요.");
    assertThat(boldTexts(message)).containsExactly("빛의 결 | 조용한 흐름");
    assertThat(message.target())
        .isEqualTo(new NotificationTarget(NotificationTargetType.MY_DISPLAYS, null));
  }

  @Test
  void requestRejectedIncludesReasonAsWritten() {
    NotificationMessage message =
        NotificationMessages.requestRejected("빛의 결", "이미지 출처를 확인할 수 없어요.");

    assertThat(message.type()).isEqualTo(NotificationType.REQUEST_REJECTED);
    assertThat(text(message))
        .isEqualTo("“빛의 결” 전시의 등록 요청이 반려됐어요. 이미지 출처를 확인할 수 없어요. 수정한 뒤 다시 요청해 주세요.");
    assertThat(boldTexts(message)).containsExactly("빛의 결");
  }

  @Test
  void exhibitionInvitedShowsInviterAsDisplayNicknameWithServiceNickname() {
    NotificationMessage message =
        NotificationMessages.exhibitionInvited("펼쳐지는 장면", "김도윤", "doyoon");

    assertThat(text(message)).isEqualTo("“펼쳐지는 장면” 전시에 팀원으로 초대받았어요. 초대한 사람: 김도윤(doyoon)");
    assertThat(boldTexts(message)).containsExactly("펼쳐지는 장면", "김도윤(doyoon)");
    assertThat(message.target())
        .isEqualTo(new NotificationTarget(NotificationTargetType.MY_INVITATIONS, null));
  }

  @Test
  void exhibitionMemberRemovedOmitsArtworkSentenceWhenNothingDeleted() {
    assertThat(text(NotificationMessages.exhibitionMemberRemoved("손끝의 지도", 2)))
        .isEqualTo("“손끝의 지도” 전시에서 내보내졌어요. 작가로 등록돼 있던 작품 2점이 함께 삭제됐어요.");
    assertThat(text(NotificationMessages.exhibitionMemberRemoved("손끝의 지도", 0)))
        .isEqualTo("“손끝의 지도” 전시에서 내보내졌어요.");
  }

  @Test
  void exhibitionDeletedOmitsArtworkSentenceWhenNoArtwork() {
    NotificationMessage message = NotificationMessages.exhibitionDeleted("움직임의 기록", 5);

    assertThat(text(message)).isEqualTo("“움직임의 기록” 전시가 삭제됐어요. 작가로 등록돼 있던 작품 5점이 함께 삭제됐어요.");
    assertThat(boldTexts(message)).containsExactly("움직임의 기록", "5점");
    assertThat(message.target()).isNull();
    assertThat(text(NotificationMessages.exhibitionDeleted("움직임의 기록", 0)))
        .isEqualTo("“움직임의 기록” 전시가 삭제됐어요.");
  }

  @Test
  void artistNewExhibitionShowsOneArtistWithPeriodAndPlace() {
    NotificationMessage message =
        NotificationMessages.artistNewExhibition(
            "송하윤",
            0,
            "재생 중",
            LocalDate.of(2026, 12, 2),
            LocalDate.of(2026, 12, 7),
            "미온미디어관 B1",
            5L);

    assertThat(text(message)).isEqualTo("송하윤님이 참여한 “재생 중” 전시가 공개됐어요. 12.02 - 12.07 · 미온미디어관 B1");
    assertThat(boldTexts(message)).containsExactly("송하윤", "재생 중");
    assertThat(message.target())
        .isEqualTo(new NotificationTarget(NotificationTargetType.DISPLAY_DETAIL, 5L));
  }

  @Test
  void artistNewExhibitionShowsOthersCountWhenManySavedArtistsJoined() {
    NotificationMessage message =
        NotificationMessages.artistNewExhibition(
            "송하윤", 1, "재생 중", LocalDate.of(2026, 12, 2), LocalDate.of(2026, 12, 7), null, 5L);

    assertThat(text(message)).isEqualTo("송하윤님 외 1명이 참여한 “재생 중” 전시가 공개됐어요. 12.02 - 12.07");
    assertThat(boldTexts(message)).containsExactly("송하윤", "1명", "재생 중");
  }

  @Test
  void exhibitionStartedShowsPeriodAndPlaceAndMovesToDisplayDetail() {
    NotificationMessage message =
        NotificationMessages.exhibitionStarted(
            "빛이 지나간 자리", LocalDate.of(2026, 12, 4), LocalDate.of(2026, 12, 6), "스튜디오 여백 B1", 5L);

    assertThat(message.type()).isEqualTo(NotificationType.EXHIBITION_STARTED);
    assertThat(message.title()).isEqualTo("저장한 전시 시작");
    assertThat(text(message)).isEqualTo("저장한 “빛이 지나간 자리” 전시가 시작됐어요. 12.04 - 12.06 · 스튜디오 여백 B1");
    assertThat(boldTexts(message)).containsExactly("빛이 지나간 자리");
    assertThat(message.target())
        .isEqualTo(new NotificationTarget(NotificationTargetType.DISPLAY_DETAIL, 5L));
  }

  private static String text(NotificationMessage message) {
    return message.body().stream().map(NotificationBodySegment::text).collect(Collectors.joining());
  }

  private static List<String> boldTexts(NotificationMessage message) {
    return message.body().stream()
        .filter(NotificationBodySegment::bold)
        .map(NotificationBodySegment::text)
        .toList();
  }
}
