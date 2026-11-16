package com.example.demo.domain.admin.infrastructure.adapter;

import com.example.demo.domain.admin.application.port.CommentQueryPort;
import com.example.demo.domain.admin.application.port.ParentQueryPort;
import com.example.demo.domain.admin.application.result.CommentDetail;
import com.example.demo.domain.admin.application.result.ParentDetail;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.SpringDataArtworkFeelingJpaRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.SpringDataArtworkFeelingReplyJpaRepository;
import com.example.demo.domain.displayartwork.infrastructure.persistence.SpringDataDisplayArtworkJpaRepository;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.SpringDataDisplayReviewJpaRepository;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.SpringDataDisplayReviewReplyJpaRepository;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.SpringDataPersonalArtworkFeelingJpaRepository;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.SpringDataPersonalArtworkFeelingReplyJpaRepository;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// 관리자 전용 상세조회용 어댑터. 삭제된 자료도 검토를 위해 조회.
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCommunicationQueryAdapter implements CommentQueryPort, ParentQueryPort {
  public static final String DISPLAY_REVIEW = "DISPLAY_REVIEW";
  public static final String ARTWORK_FEELING = "ARTWORK_FEELING";
  public static final String PERSONAL_ARTWORK_FEELING = "PERSONAL_ARTWORK_FEELING";
  public static final String DISPLAY_REVIEW_REPLY = "DISPLAY_REVIEW_REPLY";
  public static final String ARTWORK_FEELING_REPLY = "ARTWORK_FEELING_REPLY";
  public static final String PERSONAL_ARTWORK_FEELING_REPLY = "PERSONAL_ARTWORK_FEELING_REPLY";

  private final SpringDataDisplayReviewJpaRepository displayReviews;
  private final SpringDataDisplayReviewReplyJpaRepository displayReplies;
  private final SpringDataArtworkFeelingJpaRepository artworkFeelings;
  private final SpringDataArtworkFeelingReplyJpaRepository artworkReplies;
  private final SpringDataPersonalArtworkFeelingJpaRepository personalFeelings;
  private final SpringDataPersonalArtworkFeelingReplyJpaRepository personalReplies;
  private final SpringDataDisplayArtworkJpaRepository displayArtworks;
  private final UserJpaRepository users;

  @Override
  public CommentDetail getDetail(String type, Long id) {
    validateId(id);
    return switch (type) {
      case DISPLAY_REVIEW_REPLY -> displayReply(id);
      case ARTWORK_FEELING_REPLY -> artworkReply(id);
      case PERSONAL_ARTWORK_FEELING_REPLY -> personalReply(id);
      default -> throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    };
  }

  @Override
  public ParentDetail getDetail(String type, Long id) {
    validateId(id);
    return switch (type) {
      case DISPLAY_REVIEW -> {
        DisplayReview r = displayReviews.findById(id).orElseThrow(this::notFound);
        yield new ParentDetail(toParent(r), r.getDisplayId(), null);
      }
      case ARTWORK_FEELING -> {
        ArtworkFeeling f = artworkFeelings.findById(id).orElseThrow(this::notFound);
        yield new ParentDetail(
            toParent(f), displayIdOf(f.getDisplayArtworkId()), f.getDisplayArtworkId());
      }
      case PERSONAL_ARTWORK_FEELING -> {
        PersonalArtworkFeeling f = personalFeelings.findById(id).orElseThrow(this::notFound);
        yield new ParentDetail(toParent(f), null, f.getPersonalArtworkId());
      }
      default -> throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    };
  }

  private CommentDetail displayReply(Long id) {
    DisplayReviewReply r = displayReplies.findById(id).orElseThrow(this::notFound);
    DisplayReview p = displayReviews.findById(r.getDisplayReviewId()).orElse(null);
    CommentDetail.Comment comment =
        new CommentDetail.Comment(
            DISPLAY_REVIEW_REPLY,
            id,
            authorId(r.getUserId()),
            nickname(r.getUserId()),
            r.getContent(),
            r.getImages().stream()
                .map(i -> new CommentDetail.Attachment(i.getImageUrl(), "IMAGE"))
                .toList(),
            instant(r.getCreatedAt()),
            instant(r.getDeletedAt()));
    CommentDetail.Parent parent = p == null ? null : toCommentParent(p);
    return new CommentDetail(comment, parent, p == null ? null : p.getDisplayId(), null);
  }

  private CommentDetail artworkReply(Long id) {
    ArtworkFeelingReply r = artworkReplies.findById(id).orElseThrow(this::notFound);
    ArtworkFeeling p = artworkFeelings.findById(r.getFeelingId()).orElse(null);
    CommentDetail.Comment comment =
        new CommentDetail.Comment(
            ARTWORK_FEELING_REPLY,
            id,
            authorId(r.getUserId()),
            nickname(r.getUserId()),
            r.getContent(),
            r.getImages().stream()
                .map(i -> new CommentDetail.Attachment(i.getImageUrl(), "IMAGE"))
                .toList(),
            instant(r.getCreatedAt()),
            instant(r.getDeletedAt()));
    return new CommentDetail(
        comment,
        p == null ? null : toCommentParent(p),
        p == null ? null : displayIdOf(p.getDisplayArtworkId()),
        p == null ? null : p.getDisplayArtworkId());
  }

  private CommentDetail personalReply(Long id) {
    PersonalArtworkFeelingReply r = personalReplies.findById(id).orElseThrow(this::notFound);
    PersonalArtworkFeeling p = personalFeelings.findById(r.getPersonalFeelingId()).orElse(null);
    CommentDetail.Comment comment =
        new CommentDetail.Comment(
            PERSONAL_ARTWORK_FEELING_REPLY,
            id,
            authorId(r.getUserId()),
            nickname(r.getUserId()),
            r.getContent(),
            r.getImages().stream()
                .map(i -> new CommentDetail.Attachment(i.getImageUrl(), "IMAGE"))
                .toList(),
            instant(r.getCreatedAt()),
            instant(r.getDeletedAt()));
    return new CommentDetail(
        comment,
        p == null ? null : toCommentParent(p),
        null,
        p == null ? null : p.getPersonalArtworkId());
  }

  private CommentDetail.Parent toCommentParent(DisplayReview p) {
    return new CommentDetail.Parent(
        DISPLAY_REVIEW,
        p.getDisplayReviewId(),
        authorId(p.getUserId()),
        nickname(p.getUserId()),
        p.getContent(),
        p.getImages().stream()
            .map(i -> new CommentDetail.Attachment(i.getImageUrl(), "IMAGE"))
            .toList(),
        instant(p.getCreatedAt()),
        instant(p.getDeletedAt()));
  }

  private CommentDetail.Parent toCommentParent(ArtworkFeeling p) {
    return new CommentDetail.Parent(
        ARTWORK_FEELING,
        p.getFeelingId(),
        authorId(p.getUserId()),
        nickname(p.getUserId()),
        p.getContent(),
        p.getImages().stream()
            .map(i -> new CommentDetail.Attachment(i.getImageUrl(), "IMAGE"))
            .toList(),
        instant(p.getCreatedAt()),
        instant(p.getDeletedAt()));
  }

  private CommentDetail.Parent toCommentParent(PersonalArtworkFeeling p) {
    return new CommentDetail.Parent(
        PERSONAL_ARTWORK_FEELING,
        p.getPersonalFeelingId(),
        authorId(p.getUserId()),
        nickname(p.getUserId()),
        p.getContent(),
        p.getImages().stream()
            .map(i -> new CommentDetail.Attachment(i.getImageUrl(), "IMAGE"))
            .toList(),
        instant(p.getCreatedAt()),
        instant(p.getDeletedAt()));
  }

  private ParentDetail.Parent toParent(DisplayReview p) {
    return new ParentDetail.Parent(
        DISPLAY_REVIEW,
        p.getDisplayReviewId(),
        authorId(p.getUserId()),
        nickname(p.getUserId()),
        p.getContent(),
        p.getImages().stream()
            .map(i -> new ParentDetail.Attachment(i.getImageUrl(), "IMAGE"))
            .toList(),
        instant(p.getCreatedAt()),
        instant(p.getDeletedAt()));
  }

  private ParentDetail.Parent toParent(ArtworkFeeling p) {
    return new ParentDetail.Parent(
        ARTWORK_FEELING,
        p.getFeelingId(),
        authorId(p.getUserId()),
        nickname(p.getUserId()),
        p.getContent(),
        p.getImages().stream()
            .map(i -> new ParentDetail.Attachment(i.getImageUrl(), "IMAGE"))
            .toList(),
        instant(p.getCreatedAt()),
        instant(p.getDeletedAt()));
  }

  private ParentDetail.Parent toParent(PersonalArtworkFeeling p) {
    return new ParentDetail.Parent(
        PERSONAL_ARTWORK_FEELING,
        p.getPersonalFeelingId(),
        authorId(p.getUserId()),
        nickname(p.getUserId()),
        p.getContent(),
        p.getImages().stream()
            .map(i -> new ParentDetail.Attachment(i.getImageUrl(), "IMAGE"))
            .toList(),
        instant(p.getCreatedAt()),
        instant(p.getDeletedAt()));
  }

  private Long displayIdOf(Long artworkId) {
    return displayArtworks.findById(artworkId).map(a -> a.getDisplay().getId()).orElse(null);
  }

  /** 탈퇴한 작성자는 식별 정보 대신 null로 처리한다. */
  private Long authorId(Long id) {
    return users.findById(id).filter(u -> u.getDeletedAt() == null).map(User::getId).orElse(null);
  }

  private String nickname(Long id) {
    return users
        .findById(id)
        .filter(u -> u.getDeletedAt() == null)
        .map(User::getNickname)
        .orElse(null);
  }

  private Instant instant(LocalDateTime value) {
    return value == null ? null : value.toInstant(ZoneOffset.UTC);
  }

  private void validateId(Long id) {
    if (id == null || id <= 0) throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
  }

  private BusinessException notFound() {
    return new BusinessException(GlobalErrorCode.NOT_FOUND);
  }
}
