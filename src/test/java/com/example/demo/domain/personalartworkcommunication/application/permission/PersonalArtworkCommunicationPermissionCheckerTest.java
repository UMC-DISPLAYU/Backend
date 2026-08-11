package com.example.demo.domain.personalartworkcommunication.application.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkExistenceRepository;
import com.example.demo.global.error.BusinessException;
import org.junit.jupiter.api.Test;

class PersonalArtworkCommunicationPermissionCheckerTest {

  private static final Long USER_ID = 1L;
  private static final Long PERSONAL_ARTWORK_ID = 10L;

  private final PersonalArtworkExistenceRepository personalArtworkExistenceRepository =
      mock(PersonalArtworkExistenceRepository.class);
  private final PersonalArtworkCommunicationPermissionChecker checker =
      new PersonalArtworkCommunicationPermissionChecker(personalArtworkExistenceRepository);

  @Test
  void requirePersonalArtworkOwnerRejectsNonOwner() {
    assertForbidden(
        () -> checker.requirePersonalArtworkOwner(PERSONAL_ARTWORK_ID, USER_ID),
        PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_REPLY_FORBIDDEN);
  }

  @Test
  void requirePersonalFeelingWriterRejectsNonWriter() {
    PersonalArtworkFeeling feeling = mock(PersonalArtworkFeeling.class);

    assertForbidden(
        () -> checker.requirePersonalFeelingWriter(feeling, USER_ID),
        PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_FEELING_FORBIDDEN);
  }

  @Test
  void requirePersonalFeelingReplyWriterRejectsNonWriter() {
    PersonalArtworkFeelingReply reply = mock(PersonalArtworkFeelingReply.class);

    assertForbidden(
        () -> checker.requirePersonalFeelingReplyWriter(reply, USER_ID),
        PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_FEELING_REPLY_FORBIDDEN);
  }

  @Test
  void requirePersonalQuestionWriterRejectsNonWriter() {
    PersonalArtworkQuestion question = mock(PersonalArtworkQuestion.class);

    assertForbidden(
        () -> checker.requirePersonalQuestionWriter(question, USER_ID),
        PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_QUESTION_FORBIDDEN);
  }

  @Test
  void requirePersonalQuestionAccessibleAllowsPrivateQuestionForOwner() {
    PersonalArtworkQuestion question = mock(PersonalArtworkQuestion.class);

    assertThatCode(() -> checker.requirePersonalQuestionAccessible(question, USER_ID, true))
        .doesNotThrowAnyException();
  }

  @Test
  void requirePersonalQuestionAccessibleRejectsPrivateQuestionForUnrelatedViewer() {
    PersonalArtworkQuestion question = mock(PersonalArtworkQuestion.class);

    assertForbidden(
        () -> checker.requirePersonalQuestionAccessible(question, USER_ID, false),
        PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_QUESTION_FORBIDDEN);
  }

  @Test
  void requirePersonalQuestionReplyWriterRejectsNonWriter() {
    PersonalArtworkQuestionReply reply = mock(PersonalArtworkQuestionReply.class);

    assertForbidden(
        () -> checker.requirePersonalQuestionReplyWriter(reply, USER_ID),
        PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_REPLY_FORBIDDEN);
  }

  private void assertForbidden(
      Runnable invocation, PersonalArtworkCommunicationErrorCode errorCode) {
    assertThatThrownBy(invocation::run)
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception -> assertThat(exception.errorCode()).isEqualTo(errorCode));
  }
}
