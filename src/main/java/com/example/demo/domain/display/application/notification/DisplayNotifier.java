package com.example.demo.domain.display.application.notification;

import com.example.demo.domain.archive.application.result.ArtistSaverResult;
import com.example.demo.domain.archive.application.usecase.GetArtistSaversUseCase;
import com.example.demo.domain.artist.application.result.ArtistProfileSummaryResult;
import com.example.demo.domain.artist.application.usecase.GetArtistProfileSummariesUseCase;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.displayartwork.application.usecase.CountDisplayArtworksByArtistUseCase;
import com.example.demo.domain.notification.application.message.NotificationMessage;
import com.example.demo.domain.notification.application.message.NotificationMessages;
import com.example.demo.domain.notification.application.usecase.RequestNotificationUseCase;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 전시 도메인에서 일어난 일을 알림으로 요청한다. 받는 사람과 문구를 정하는 일만 하고, 저장은 알림 모듈이 커밋 후 비동기로 한다. 그래서 반드시 본 작업의 트랜잭션 안에서
 * 호출한다.
 */
@Component
public class DisplayNotifier {

  private final RequestNotificationUseCase requestNotificationUseCase;
  private final UserRepository userRepository;
  private final CountDisplayArtworksByArtistUseCase countDisplayArtworksByArtistUseCase;
  private final GetArtistSaversUseCase getArtistSaversUseCase;
  private final GetArtistProfileSummariesUseCase getArtistProfileSummariesUseCase;

  public DisplayNotifier(
      RequestNotificationUseCase requestNotificationUseCase,
      UserRepository userRepository,
      CountDisplayArtworksByArtistUseCase countDisplayArtworksByArtistUseCase,
      GetArtistSaversUseCase getArtistSaversUseCase,
      GetArtistProfileSummariesUseCase getArtistProfileSummariesUseCase) {
    this.requestNotificationUseCase = requestNotificationUseCase;
    this.userRepository = userRepository;
    this.countDisplayArtworksByArtistUseCase = countDisplayArtworksByArtistUseCase;
    this.getArtistSaversUseCase = getArtistSaversUseCase;
    this.getArtistProfileSummariesUseCase = getArtistProfileSummariesUseCase;
  }

  /** 등록 승인은 대표자에게, 전시 공개는 참여 작가를 저장한 사용자에게 알린다. 승인되는 순간 전시가 공개된다. */
  public void notifyReviewApproved(Display display, Long reviewerId) {
    findLeaderUserId(display)
        .ifPresent(
            leaderUserId ->
                requestNotificationUseCase.request(
                    List.of(leaderUserId),
                    reviewerId,
                    NotificationMessages.requestApproved(display.getTitle())));
    notifyArtistSavers(display);
  }

  public void notifyReviewRejected(Display display, Long reviewerId, String rejectionReason) {
    findLeaderUserId(display)
        .ifPresent(
            leaderUserId ->
                requestNotificationUseCase.request(
                    List.of(leaderUserId),
                    reviewerId,
                    NotificationMessages.requestRejected(display.getTitle(), rejectionReason)));
  }

  /** 초대한 사람은 이 전시의 작가명과 서비스 닉네임을 함께 보여준다. */
  public void notifyInvited(Display display, Long inviterUserId, Long inviteeUserId) {
    String inviterDisplayNickname =
        acceptedMembers(display).stream()
            .filter(member -> member.getUserId().value().equals(inviterUserId))
            .map(TeamMember::getDisplayNickname)
            .findFirst()
            .orElse("");
    String inviterNickname =
        userRepository.findById(inviterUserId).map(User::getNickname).orElse("");
    requestNotificationUseCase.request(
        List.of(inviteeUserId),
        inviterUserId,
        NotificationMessages.exhibitionInvited(
            display.getTitle(), inviterDisplayNickname, inviterNickname));
  }

  public void notifyMemberRemoved(
      Display display, Long removedUserId, Long requesterUserId, int deletedArtworkCount) {
    requestNotificationUseCase.request(
        List.of(removedUserId),
        requesterUserId,
        NotificationMessages.exhibitionMemberRemoved(display.getTitle(), deletedArtworkCount));
  }

  /** 작품 정리는 커밋 뒤에 따로 돌기 때문에, 팀원별로 "작가로 등록돼 있던 작품 수"를 삭제 트랜잭션 안에서 먼저 센다. 작품 삭제 알림은 따로 보내지 않는다. */
  public void notifyDisplayDeleted(Display display, Long deleterUserId) {
    Map<Long, Integer> artworkCountByUserId =
        countDisplayArtworksByArtistUseCase.countByArtist(display.getId());
    Map<Integer, List<Long>> recipientsByArtworkCount =
        acceptedMembers(display).stream()
            .map(member -> member.getUserId().value())
            .collect(
                Collectors.groupingBy(
                    userId -> artworkCountByUserId.getOrDefault(userId, 0),
                    LinkedHashMap::new,
                    Collectors.toList()));
    recipientsByArtworkCount.forEach(
        (artworkCount, recipients) ->
            requestNotificationUseCase.request(
                recipients,
                deleterUserId,
                NotificationMessages.exhibitionDeleted(display.getTitle(), artworkCount)));
  }

  /**
   * 참여 작가(수락한 팀원)를 저장한 사용자에게 받는 사람당 한 건씩 알린다. 여러 명을 저장했다면 가장 먼저 저장한 작가 이름에 "외 N명"을 붙인다. 이 전시에 참여한
   * 사람은 이미 알고 있으므로 받지 않는다.
   */
  private void notifyArtistSavers(Display display) {
    Set<Long> memberUserIds =
        acceptedMembers(display).stream()
            .map(member -> member.getUserId().value())
            .collect(Collectors.toSet());
    if (memberUserIds.isEmpty()) {
      return;
    }

    Map<Long, List<ArtistSaverResult>> savedArtistsByUserId =
        getArtistSaversUseCase.getArtistSavers(memberUserIds).stream()
            .filter(saver -> !memberUserIds.contains(saver.userId()))
            .collect(Collectors.groupingBy(ArtistSaverResult::userId));
    if (savedArtistsByUserId.isEmpty()) {
      return;
    }

    Map<Long, String> artistNameByProfileId = artistNames(savedArtistsByUserId);
    Map<NotificationMessage, List<Long>> recipientsByMessage = new LinkedHashMap<>();
    savedArtistsByUserId.forEach(
        (userId, savedArtists) -> {
          ArtistSaverResult first =
              savedArtists.stream()
                  .min(Comparator.comparing(ArtistSaverResult::savedAt))
                  .orElseThrow();
          NotificationMessage message =
              NotificationMessages.artistNewExhibition(
                  artistNameByProfileId.getOrDefault(first.artistProfileId(), ""),
                  savedArtists.size() - 1,
                  display.getTitle(),
                  display.getPeriod().startDate(),
                  display.getPeriod().endDate(),
                  display.getLocation() == null ? null : display.getLocation().placeName(),
                  display.getId());
          recipientsByMessage.computeIfAbsent(message, key -> new ArrayList<>()).add(userId);
        });
    // 같은 문구를 받는 사람끼리 묶어 한 번에 요청한다. 저장한 사람이 많아도 요청 횟수는 문구 종류 수만큼이다.
    recipientsByMessage.forEach(
        (message, recipients) -> requestNotificationUseCase.request(recipients, null, message));
  }

  private Map<Long, String> artistNames(Map<Long, List<ArtistSaverResult>> savedArtistsByUserId) {
    List<Long> artistProfileIds =
        savedArtistsByUserId.values().stream()
            .flatMap(List::stream)
            .map(ArtistSaverResult::artistProfileId)
            .distinct()
            .toList();
    return getArtistProfileSummariesUseCase.getArtistProfileSummaries(artistProfileIds).stream()
        .collect(
            Collectors.toMap(
                ArtistProfileSummaryResult::artistProfileId,
                ArtistProfileSummaryResult::artistName,
                (left, right) -> left));
  }

  private static Optional<Long> findLeaderUserId(Display display) {
    return acceptedMembers(display).stream()
        .filter(member -> member.getRole() == TeamMemberRole.TEAM_LEADER)
        .map(member -> member.getUserId().value())
        .findFirst();
  }

  private static List<TeamMember> acceptedMembers(Display display) {
    return display.getTeamMembers().stream()
        .filter(TeamMember::isAccepted)
        .filter(member -> !member.isDeleted())
        .toList();
  }
}
