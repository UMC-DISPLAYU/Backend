package com.example.demo.domain.admin.infrastructure.adapter;

import static com.example.demo.domain.admin.infrastructure.adapter.AdminCommunicationQueryAdapter.ARTWORK_FEELING_REPLY;
import static com.example.demo.domain.admin.infrastructure.adapter.AdminCommunicationQueryAdapter.DISPLAY_REVIEW_REPLY;
import static com.example.demo.domain.admin.infrastructure.adapter.AdminCommunicationQueryAdapter.PERSONAL_ARTWORK_FEELING_REPLY;

import com.example.demo.domain.admin.application.port.CommentDeletionPort;
import com.example.demo.domain.admin.application.result.CommentDeletionResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.infrastructure.persistence.SpringDataArtworkFeelingReplyJpaRepository;
import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
import com.example.demo.domain.displaycommunication.infrastructure.persistence.SpringDataDisplayReviewReplyJpaRepository;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.infrastructure.persistence.SpringDataPersonalArtworkFeelingReplyJpaRepository;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminCommentDeletionAdapter implements CommentDeletionPort {
  private final SpringDataDisplayReviewReplyJpaRepository displayReplies;
  private final SpringDataArtworkFeelingReplyJpaRepository artworkReplies;
  private final SpringDataPersonalArtworkFeelingReplyJpaRepository personalReplies;

  @Override
  @Transactional
  public CommentDeletionResult delete(
      String commentType, Long commentId, Long operatorId, String reason) {
    LocalDateTime deletedAt =
        switch (commentType) {
          case DISPLAY_REVIEW_REPLY -> deleteDisplayReply(commentId);
          case ARTWORK_FEELING_REPLY -> deleteArtworkReply(commentId);
          case PERSONAL_ARTWORK_FEELING_REPLY -> deletePersonalReply(commentId);
          default -> throw new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE);
        };
    return new CommentDeletionResult(commentId, deletedAt.toInstant(ZoneOffset.UTC));
  }

  private LocalDateTime deleteDisplayReply(Long id) {
    DisplayReviewReply reply =
        displayReplies.findActiveByIdForUpdate(id).orElseThrow(this::notFound);
    reply.delete();
    return displayReplies.save(reply).getDeletedAt();
  }

  private LocalDateTime deleteArtworkReply(Long id) {
    ArtworkFeelingReply reply =
        artworkReplies.findActiveByIdForUpdate(id).orElseThrow(this::notFound);
    reply.delete();
    return artworkReplies.save(reply).getDeletedAt();
  }

  private LocalDateTime deletePersonalReply(Long id) {
    PersonalArtworkFeelingReply reply =
        personalReplies.findActiveByIdForUpdate(id).orElseThrow(this::notFound);
    reply.delete();
    return personalReplies.save(reply).getDeletedAt();
  }

  private BusinessException notFound() {
    return new BusinessException(GlobalErrorCode.NOT_FOUND);
  }
}
