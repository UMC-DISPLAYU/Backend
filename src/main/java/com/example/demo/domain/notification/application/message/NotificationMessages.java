package com.example.demo.domain.notification.application.message;

import static com.example.demo.domain.notification.domain.vo.NotificationBodySegment.bold;
import static com.example.demo.domain.notification.domain.vo.NotificationBodySegment.plain;

import com.example.demo.domain.notification.domain.type.NotificationTargetType;
import com.example.demo.domain.notification.domain.type.NotificationType;
import com.example.demo.domain.notification.domain.vo.NotificationBodySegment;
import com.example.demo.domain.notification.domain.vo.NotificationTarget;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** 알림 종류별 제목과 내용 문구. 문구는 PM 알림함 명세(noti-demo)를 따르고, 이름·작품명·전시명·숫자는 굵게 강조한다. 문구가 바뀌면 이 파일만 고친다. */
public final class NotificationMessages {

  private static final String OPEN_QUOTE = "“";
  private static final String CLOSE_QUOTE = "”";

  private static final DateTimeFormatter PERIOD_FORMAT = DateTimeFormatter.ofPattern("MM.dd");

  private NotificationMessages() {}

  public static NotificationMessage requestApproved(String displayTitle) {
    return new NotificationMessage(
        NotificationType.REQUEST_APPROVED,
        "등록 요청 승인",
        List.of(
            plain(OPEN_QUOTE),
            bold(displayTitle),
            plain(CLOSE_QUOTE + " 전시의 등록 요청이 승인됐어요. 승인된 시점에 저장돼 있던 내용이 공개돼요.")),
        new NotificationTarget(NotificationTargetType.MY_DISPLAYS, null));
  }

  /** 반려 사유는 운영자가 쓴 그대로 넣는다. */
  public static NotificationMessage requestRejected(String displayTitle, String rejectionReason) {
    return new NotificationMessage(
        NotificationType.REQUEST_REJECTED,
        "등록 요청 반려",
        List.of(
            plain(OPEN_QUOTE),
            bold(displayTitle),
            plain(CLOSE_QUOTE + " 전시의 등록 요청이 반려됐어요. "),
            plain(rejectionReason.strip()),
            plain(" 수정한 뒤 다시 요청해 주세요.")),
        new NotificationTarget(NotificationTargetType.MY_DISPLAYS, null));
  }

  /** 초대한 사람은 "전시 작가명(서비스 닉네임)"으로 표시한다. 관리자 역할은 초대 후 권한 변경으로만 주므로 문구는 항상 팀원이다. */
  public static NotificationMessage exhibitionInvited(
      String displayTitle, String inviterDisplayNickname, String inviterNickname) {
    return new NotificationMessage(
        NotificationType.EXHIBITION_INVITED,
        "전시 초대",
        List.of(
            plain(OPEN_QUOTE),
            bold(displayTitle),
            plain(CLOSE_QUOTE + " 전시에 팀원으로 초대받았어요. 초대한 사람: "),
            bold(inviterDisplayNickname + "(" + inviterNickname + ")")),
        new NotificationTarget(NotificationTargetType.MY_INVITATIONS, null));
  }

  /** 함께 삭제된 작품이 없으면 뒷문장을 뺀다. */
  public static NotificationMessage exhibitionMemberRemoved(
      String displayTitle, int deletedArtworkCount) {
    List<NotificationBodySegment> body = new ArrayList<>();
    body.add(plain(OPEN_QUOTE));
    body.add(bold(displayTitle));
    body.add(plain(CLOSE_QUOTE + " 전시에서 내보내졌어요."));
    appendDeletedArtworkCount(body, deletedArtworkCount);
    return new NotificationMessage(
        NotificationType.EXHIBITION_MEMBER_REMOVED, "전시에서 내보내짐", body, null);
  }

  /** 받는 사람이 작가로 등록돼 있던 작품 수를 함께 알린다. 작품이 없으면 뒷문장을 뺀다. */
  public static NotificationMessage exhibitionDeleted(
      String displayTitle, int deletedArtworkCount) {
    List<NotificationBodySegment> body = new ArrayList<>();
    body.add(plain(OPEN_QUOTE));
    body.add(bold(displayTitle));
    body.add(plain(CLOSE_QUOTE + " 전시가 삭제됐어요."));
    appendDeletedArtworkCount(body, deletedArtworkCount);
    return new NotificationMessage(NotificationType.EXHIBITION_DELETED, "전시 삭제", body, null);
  }

  /**
   * 받는 사람이 저장한 작가 중 이 전시에 참여한 사람이 여러 명이면 "A님 외 N명"으로 표시한다. 장소가 없으면 기간만 붙인다.
   *
   * @param otherArtistCount 대표로 이름을 보인 작가 외에 받는 사람이 저장한 참여 작가 수
   */
  public static NotificationMessage artistNewExhibition(
      String artistName,
      int otherArtistCount,
      String displayTitle,
      LocalDate startDate,
      LocalDate endDate,
      String placeName,
      Long displayId) {
    List<NotificationBodySegment> body = new ArrayList<>();
    body.add(bold(artistName));
    body.add(plain("님"));
    if (otherArtistCount > 0) {
      body.add(plain(" 외 "));
      body.add(bold(otherArtistCount + "명"));
    }
    body.add(plain("이 참여한 " + OPEN_QUOTE));
    body.add(bold(displayTitle));
    String period = PERIOD_FORMAT.format(startDate) + " - " + PERIOD_FORMAT.format(endDate);
    String place = placeName == null || placeName.isBlank() ? "" : " · " + placeName;
    body.add(plain(CLOSE_QUOTE + " 전시가 공개됐어요. " + period + place));
    return new NotificationMessage(
        NotificationType.ARTIST_NEW_EXHIBITION,
        "저장한 작가의 새 전시",
        body,
        new NotificationTarget(NotificationTargetType.DISPLAY_DETAIL, displayId));
  }

  private static void appendDeletedArtworkCount(
      List<NotificationBodySegment> body, int deletedArtworkCount) {
    if (deletedArtworkCount <= 0) {
      return;
    }
    body.add(plain(" 작가로 등록돼 있던 작품 "));
    body.add(bold(deletedArtworkCount + "점"));
    body.add(plain("이 함께 삭제됐어요."));
  }

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

  /** 장소가 없으면 기간만 붙인다. */
  public static NotificationMessage exhibitionStarted(
      String displayTitle,
      LocalDate startDate,
      LocalDate endDate,
      String placeName,
      Long displayId) {
    String period =
        STARTED_PERIOD_FORMAT.format(startDate) + " - " + STARTED_PERIOD_FORMAT.format(endDate);
    String place = placeName == null || placeName.isBlank() ? "" : " · " + placeName;
    return new NotificationMessage(
        NotificationType.EXHIBITION_STARTED,
        "저장한 전시 시작",
        List.of(
            plain("저장한 " + OPEN_QUOTE),
            bold(displayTitle),
            plain(CLOSE_QUOTE + " 전시가 시작됐어요. " + period + place)),
        new NotificationTarget(NotificationTargetType.DISPLAY_DETAIL, displayId));
  }

  private static final DateTimeFormatter STARTED_PERIOD_FORMAT =
      DateTimeFormatter.ofPattern("MM.dd");
}
