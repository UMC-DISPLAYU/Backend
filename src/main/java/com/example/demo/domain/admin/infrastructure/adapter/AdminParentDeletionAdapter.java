package com.example.demo.domain.admin.infrastructure.adapter;

import static com.example.demo.domain.admin.infrastructure.adapter.AdminCommunicationQueryAdapter.ARTWORK_FEELING;
import static com.example.demo.domain.admin.infrastructure.adapter.AdminCommunicationQueryAdapter.DISPLAY_REVIEW;
import static com.example.demo.domain.admin.infrastructure.adapter.AdminCommunicationQueryAdapter.PERSONAL_ARTWORK_FEELING;

import com.example.demo.domain.admin.application.port.ParentDeletionPort;
import com.example.demo.domain.admin.application.result.ParentDeletionResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.SpringDataArtworkFeelingJpaRepository;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.SpringDataArtworkFeelingReplyJpaRepository;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.SpringDataDisplayReviewJpaRepository;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.SpringDataDisplayReviewReplyJpaRepository;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.SpringDataPersonalArtworkFeelingJpaRepository;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.SpringDataPersonalArtworkFeelingReplyJpaRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminParentDeletionAdapter implements ParentDeletionPort {
  private final SpringDataDisplayReviewJpaRepository displayReviews;
  private final SpringDataDisplayReviewReplyJpaRepository displayReplies;
  private final SpringDataArtworkFeelingJpaRepository artworkFeelings;
  private final SpringDataArtworkFeelingReplyJpaRepository artworkReplies;
  private final SpringDataPersonalArtworkFeelingJpaRepository personalFeelings;
  private final SpringDataPersonalArtworkFeelingReplyJpaRepository personalReplies;

  @Override
  @Transactional
  public ParentDeletionResult delete(
      String parentType, Long parentId, Long operatorId, String reason) {
    LocalDateTime deletedAt =
        switch (parentType) {
          case DISPLAY_REVIEW -> deleteDisplayReview(parentId);
          case ARTWORK_FEELING -> deleteArtworkFeeling(parentId);
          case PERSONAL_ARTWORK_FEELING -> deletePersonalFeeling(parentId);
          default -> throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
        };
    return new ParentDeletionResult(parentId, deletedAt.toInstant(ZoneOffset.UTC));
  }

  private LocalDateTime deleteDisplayReview(Long id) {
    DisplayReview review = displayReviews.findActiveByIdForUpdate(id).orElseThrow(this::notFound);
    List<DisplayReviewReply> replies = displayReplies.findActiveByDisplayReviewIdForUpdate(id);
    replies.forEach(DisplayReviewReply::delete);
    displayReplies.saveAll(replies);
    review.delete();
    return displayReviews.save(review).getDeletedAt();
  }

  private LocalDateTime deleteArtworkFeeling(Long id) {
    ArtworkFeeling feeling =
        artworkFeelings.findActiveByIdForUpdate(id).orElseThrow(this::notFound);
    List<ArtworkFeelingReply> replies = artworkReplies.findActiveByFeelingIdForUpdate(id);
    replies.forEach(ArtworkFeelingReply::delete);
    artworkReplies.saveAll(replies);
    feeling.delete();
    return artworkFeelings.save(feeling).getDeletedAt();
  }

  private LocalDateTime deletePersonalFeeling(Long id) {
    PersonalArtworkFeeling feeling =
        personalFeelings.findActiveByIdForUpdate(id).orElseThrow(this::notFound);
    List<PersonalArtworkFeelingReply> replies =
        personalReplies.findActiveByPersonalFeelingIdForUpdate(id);
    replies.forEach(PersonalArtworkFeelingReply::delete);
    personalReplies.saveAll(replies);
    feeling.delete();
    return personalFeelings.save(feeling).getDeletedAt();
  }

  private BusinessException notFound() {
    return new BusinessException(GlobalErrorCode.NOT_FOUND);
  }
}
