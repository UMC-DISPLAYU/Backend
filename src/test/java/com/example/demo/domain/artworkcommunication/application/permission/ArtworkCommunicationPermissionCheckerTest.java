package com.example.demo.domain.artworkcommunication.application.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository.ContactCreator;
import com.example.demo.global.error.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ArtworkCommunicationPermissionCheckerTest {

  private static final Long USER_ID = 1L;
  private static final Long DISPLAY_ARTWORK_ID = 10L;

  private final CreatorExistenceRepository creatorExistenceRepository =
      mock(CreatorExistenceRepository.class);
  private final ArtworkCommunicationPermissionChecker checker =
      new ArtworkCommunicationPermissionChecker(creatorExistenceRepository);

  @Test
  void requireFeelingWriterRejectsNonWriter() {
    ArtworkFeeling feeling = mock(ArtworkFeeling.class);

    assertForbidden(
        () -> checker.requireFeelingWriter(feeling, USER_ID),
        ArtworkCommunicationErrorCode.ARTWORK_FEELING_FORBIDDEN);
  }

  @Test
  void requireFeelingReplyWriterRejectsNonWriter() {
    ArtworkFeelingReply reply = mock(ArtworkFeelingReply.class);

    assertForbidden(
        () -> checker.requireFeelingReplyWriter(reply, USER_ID),
        ArtworkCommunicationErrorCode.ARTWORK_FEELING_REPLY_FORBIDDEN);
  }

  @Test
  void requireQuestionWriterRejectsNonWriter() {
    ArtworkQuestion question = mock(ArtworkQuestion.class);

    assertForbidden(
        () -> checker.requireQuestionWriter(question, USER_ID),
        ArtworkCommunicationErrorCode.ARTWORK_QUESTION_FORBIDDEN);
  }

  @Test
  void requireQuestionAccessibleAllowsPrivateQuestionForParticipant() {
    ArtworkQuestion question = mock(ArtworkQuestion.class);

    assertThatCode(() -> checker.requireQuestionAccessible(question, USER_ID, true))
        .doesNotThrowAnyException();
  }

  @Test
  void isQuestionAccessibleUsesSamePolicyAsRequiredAccess() {
    ArtworkQuestion question = mock(ArtworkQuestion.class);

    assertThat(checker.isQuestionAccessible(question, USER_ID, true)).isTrue();
    assertThat(checker.isQuestionAccessible(question, USER_ID, false)).isFalse();
  }

  @Test
  void requireQuestionAccessibleRejectsPrivateQuestionForUnrelatedViewer() {
    ArtworkQuestion question = mock(ArtworkQuestion.class);

    assertForbidden(
        () -> checker.requireQuestionAccessible(question, USER_ID, false),
        ArtworkCommunicationErrorCode.ARTWORK_QUESTION_FORBIDDEN);
  }

  @Test
  void requireQuestionReplyWriterRejectsNonWriter() {
    ArtworkQuestionReply reply = mock(ArtworkQuestionReply.class);

    assertForbidden(
        () -> checker.requireQuestionReplyWriter(reply, USER_ID),
        ArtworkCommunicationErrorCode.ARTWORK_QUESTION_REPLY_FORBIDDEN);
  }

  @Test
  void requireQnaHandlerReturnsContactCreator() {
    ContactCreator contactCreator = new ContactCreator(3L, "담당자");
    when(creatorExistenceRepository.findContactCreatorByDisplayArtworkIdAndUserId(
            DISPLAY_ARTWORK_ID, USER_ID))
        .thenReturn(Optional.of(contactCreator));

    assertThat(checker.requireQnaHandler(DISPLAY_ARTWORK_ID, USER_ID)).isEqualTo(contactCreator);
  }

  @Test
  void requireQnaHandlerRejectsNonHandler() {
    assertForbidden(
        () -> checker.requireQnaHandler(DISPLAY_ARTWORK_ID, USER_ID),
        ArtworkCommunicationErrorCode.QNA_CONTACT_FORBIDDEN);
  }

  @Test
  void requireQuestionReplyDeletionHandlerKeepsExistingForbiddenError() {
    assertForbidden(
        () -> checker.requireQuestionReplyDeletionHandler(DISPLAY_ARTWORK_ID, USER_ID),
        ArtworkCommunicationErrorCode.ARTWORK_QUESTION_REPLY_FORBIDDEN);
  }

  private void assertForbidden(Runnable invocation, ArtworkCommunicationErrorCode errorCode) {
    assertThatThrownBy(invocation::run)
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception -> assertThat(exception.errorCode()).isEqualTo(errorCode));
  }
}
