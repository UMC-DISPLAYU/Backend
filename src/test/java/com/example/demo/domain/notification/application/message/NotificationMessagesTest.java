package com.example.demo.domain.notification.application.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.notification.domain.type.NotificationTargetType;
import com.example.demo.domain.notification.domain.type.NotificationType;
import com.example.demo.domain.notification.domain.vo.NotificationBodySegment;
import com.example.demo.domain.notification.domain.vo.NotificationTarget;
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
