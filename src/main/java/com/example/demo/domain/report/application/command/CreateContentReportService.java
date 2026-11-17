package com.example.demo.domain.report.application.command;

import com.example.demo.domain.artworkcommunication.application.command.ArtworkFeelingValidator;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import com.example.demo.domain.displaycommunication.application.command.DisplayReviewValidator;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.report.application.result.CreateContentReportResult;
import com.example.demo.domain.report.domain.model.ContentReport;
import com.example.demo.domain.report.domain.repository.ContentReportRepository;
import com.example.demo.domain.report.domain.type.ReportReason;
import com.example.demo.domain.report.domain.type.ReportTargetType;
import com.example.demo.global.error.BusinessException;
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
