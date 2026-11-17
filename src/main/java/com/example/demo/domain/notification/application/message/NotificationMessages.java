package com.example.demo.domain.notification.application.message;

import static com.example.demo.domain.notification.domain.vo.NotificationBodySegment.bold;
import static com.example.demo.domain.notification.domain.vo.NotificationBodySegment.plain;

import com.example.demo.domain.notification.domain.type.NotificationTargetType;
import com.example.demo.domain.notification.domain.type.NotificationType;
import com.example.demo.domain.notification.domain.vo.NotificationBodySegment;
import com.example.demo.domain.notification.domain.vo.NotificationTarget;
import java.util.ArrayList;
import java.util.List;

/** 알림 종류별 제목과 내용 문구. 문구는 PM 알림함 명세(noti-demo)를 따르고, 이름·작품명·전시명·숫자는 굵게 강조한다. 문구가 바뀌면 이 파일만 고친다. */
public final class NotificationMessages {

  private static final String OPEN_QUOTE = "“";
  private static final String CLOSE_QUOTE = "”";

  private NotificationMessages() {}

  public static NotificationMessage coAuthorAdded(String artworkName, Long displayId) {
    return new NotificationMessage(
        NotificationType.WORK_COAUTHOR_ADDED,
        "공동 작가로 추가됨",
        List.of(
            plain(OPEN_QUOTE),
            bold(artworkName),
            plain(CLOSE_QUOTE + " 작품에 공동 작가로 추가됐어요. 작가와 같은 권한으로 수정·삭제할 수 있어요.")),
        new NotificationTarget(NotificationTargetType.DISPLAY_WORKS, displayId));
  }

  public static NotificationMessage proxyAssigned(
      String artworkName, String registrantName, Long displayId) {
    return new NotificationMessage(
        NotificationType.WORK_PROXY_ASSIGNED,
        "작가로 지정됨",
        List.of(
            plain(OPEN_QUOTE),
            bold(artworkName),
            plain(CLOSE_QUOTE + " 작품의 작가로 지정됐어요. 등록한 사람: "),
            bold(registrantName)),
        new NotificationTarget(NotificationTargetType.DISPLAY_WORKS, displayId));
  }

  public static NotificationMessage artworkDeleted(String artworkName, String deleterName) {
    return new NotificationMessage(
        NotificationType.WORK_DELETED,
        "작품 삭제",
        List.of(
            plain(OPEN_QUOTE),
            bold(artworkName),
            plain(CLOSE_QUOTE + " 작품이 삭제됐어요. 삭제한 사람: "),
            bold(deleterName),
            plain(". 복구할 수 없어요.")),
        null);
  }

  /** 작품이 1점이면 작품명만, 여러 점이면 "작품 N점(“A”, “B”)" 형태로 모든 작품명을 나열한다. */
  public static NotificationMessage artworksDeletedByArtistLeave(
      List<String> artworkNames, String displayTitle) {
    if (artworkNames == null || artworkNames.isEmpty()) {
      throw new IllegalArgumentException("artworkNames must not be empty.");
    }
    List<NotificationBodySegment> body = new ArrayList<>();
    if (artworkNames.size() == 1) {
      body.add(plain(OPEN_QUOTE));
      body.add(bold(artworkNames.get(0)));
      body.add(plain(CLOSE_QUOTE + " 작품이 삭제됐어요."));
    } else {
      body.add(plain("작품 "));
      body.add(bold(artworkNames.size() + "점"));
      body.add(plain("("));
      for (int i = 0; i < artworkNames.size(); i++) {
        body.add(plain((i == 0 ? "" : ", ") + OPEN_QUOTE));
        body.add(bold(artworkNames.get(i)));
        body.add(plain(CLOSE_QUOTE));
      }
      body.add(plain(")이 삭제됐어요."));
    }
    body.add(plain(" 함께 작가로 등록된 계정이 " + OPEN_QUOTE));
    body.add(bold(displayTitle));
    body.add(plain(CLOSE_QUOTE + " 전시에서 나가거나 내보내져 작품도 함께 삭제됐어요. 복구할 수 없어요."));
    return new NotificationMessage(
        NotificationType.WORK_DELETED_BY_ARTIST_LEAVE, "작품 삭제", body, null);
  }
}
