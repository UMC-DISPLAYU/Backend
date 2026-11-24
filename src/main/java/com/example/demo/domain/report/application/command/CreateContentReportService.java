package com.example.demo.domain.report.application.command;

import com.example.demo.domain.artworkcommunication.application.command.ArtworkFeelingValidator;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import com.example.demo.domain.displaycommunication.application.command.DisplayReviewValidator;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkFeelingValidator;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.report.application.result.CreateCommentReportResult;
import com.example.demo.domain.report.application.result.CreateContentReportResult;
import com.example.demo.domain.report.domain.model.ContentReport;
import com.example.demo.domain.report.domain.repository.ContentReportRepository;
import com.example.demo.domain.report.domain.type.ReportReason;
import com.example.demo.domain.report.domain.type.ReportTargetType;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateContentReportService {
  private final DisplayReviewValidator reviewValidator;
  private final ArtworkFeelingValidator feelingValidator;
  private final ArtworkFeelingRepository feelingRepository;
  private final PersonalArtworkFeelingValidator personalFeelingValidator;
  private final ContentReportRepository reportRepository;
  private final Clock clock;

  @Transactional
  public CreateContentReportResult reportReview(ReportDisplayReviewCommand command) {
    reviewValidator.validateDisplayExists(command.displayId());
    reviewValidator.validateUserExists(command.reporterUserId());
    DisplayReview review = reviewValidator.findReviewOrThrow(command.reviewId());
    reviewValidator.validateReviewTarget(review, command.displayId());
    return save(
        ReportTargetType.DISPLAY_REVIEW,
        review.getDisplayReviewId(),
        command.reporterUserId(),
        review.getUserId(),
        command.reason());
  }

  @Transactional
  public CreateContentReportResult reportFeeling(ReportArtworkFeelingCommand command) {
    feelingValidator.validateDisplayArtworkExists(command.artworkId());
    feelingValidator.validateUserExists(command.reporterUserId());
    ArtworkFeeling feeling =
        feelingRepository
            .findById(command.feelingId())
            .orElseThrow(
                () -> new BusinessException(ArtworkCommunicationErrorCode.FEELING_NOT_FOUND));
    feelingValidator.validateFeelingTarget(feeling, command.artworkId());
    return save(
        ReportTargetType.ARTWORK_FEELING,
        feeling.getFeelingId(),
        command.reporterUserId(),
        feeling.getUserId(),
        command.reason());
  }

  @Transactional
  public CreateCommentReportResult reportComment(ReportCommentCommand command) {
    ReportTargetType targetType = parseCommentType(command.commentType());
    reviewValidator.validateUserExists(command.reporterUserId());
    Long authorId = findActiveCommentAuthor(targetType, command.commentId());
    String description = normalizeDescription(command.description());
    ContentReport report =
        reportRepository.save(
            ContentReport.create(
                targetType,
                command.commentId(),
                command.reporterUserId(),
                authorId,
                command.reasonCode(),
                description,
                LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC)));
    return new CreateCommentReportResult(
        report.reportId(), report.createdAt().toInstant(ZoneOffset.UTC));
  }

  private ReportTargetType parseCommentType(String commentType) {
    try {
      ReportTargetType type = ReportTargetType.valueOf(commentType);
      return switch (type) {
        case DISPLAY_REVIEW_REPLY, ARTWORK_FEELING_REPLY, PERSONAL_ARTWORK_FEELING_REPLY -> type;
        default -> throw new IllegalArgumentException();
      };
    } catch (IllegalArgumentException | NullPointerException exception) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
  }

  private Long findActiveCommentAuthor(ReportTargetType type, Long commentId) {
    if (commentId == null || commentId <= 0) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    return switch (type) {
      case DISPLAY_REVIEW_REPLY -> {
        DisplayReviewReply reply = reviewValidator.findReplyOrThrow(commentId);
        yield reply.getUserId();
      }
      case ARTWORK_FEELING_REPLY -> {
        ArtworkFeelingReply reply = feelingValidator.findReplyOrThrow(commentId);
        yield reply.getUserId();
      }
      case PERSONAL_ARTWORK_FEELING_REPLY -> {
        PersonalArtworkFeelingReply reply = personalFeelingValidator.findReplyOrThrow(commentId);
        yield reply.getUserId();
      }
      default -> throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    };
  }

  private String normalizeDescription(String description) {
    if (description == null) return null;
    String normalized = description.strip();
    if (normalized.length() > 1000) {
      throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
    }
    return normalized.isEmpty() ? null : normalized;
  }

  private CreateContentReportResult save(
      ReportTargetType type, Long targetId, Long reporterId, Long authorId, ReportReason reason) {
    ContentReport report =
        reportRepository.save(
            ContentReport.create(
                type,
                targetId,
                reporterId,
                authorId,
                reason,
                LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC)));
    return new CreateContentReportResult(report.reportId(), report.createdAt());
  }
}
