package com.example.demo.domain.personalartworkcommunication.application.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.personalartwork.application.result.PersonalArtworkAccessResult;
import com.example.demo.domain.personalartwork.application.usecase.GetPersonalArtworkAccessUseCase;
import com.example.demo.domain.personalartworkcommunication.application.permission.PersonalArtworkCommunicationPermissionChecker;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeletePersonalArtworkQuestionServiceTest {

  @Mock private GetPersonalArtworkAccessUseCase getPersonalArtworkAccessUseCase;
  @Mock private UserExistenceRepository userExistenceRepository;
  @Mock private PersonalArtworkQuestionReplyRepository personalArtworkQuestionReplyRepository;
  @Mock private PersonalArtworkQuestionRepository personalArtworkQuestionRepository;

  private DeletePersonalArtworkQuestionService service;

  @BeforeEach
  void setUp() {
    PersonalArtworkQuestionValidator validator =
        new PersonalArtworkQuestionValidator(
            getPersonalArtworkAccessUseCase,
            userExistenceRepository,
            personalArtworkQuestionReplyRepository,
            personalArtworkQuestionRepository);
    service =
        new DeletePersonalArtworkQuestionService(
            validator,
            new PersonalArtworkCommunicationPermissionChecker(getPersonalArtworkAccessUseCase),
            personalArtworkQuestionRepository);
  }

  @Test
  void answeredQuestionCannotBeDeleted() {
    PersonalArtworkQuestion question =
        PersonalArtworkQuestion.create(1L, 2L, "답변이 등록된 질문", true, List.of());
    question.answer(3L, "답변", List.of());
    when(getPersonalArtworkAccessUseCase.getPersonalArtworkAccess(1L))
        .thenReturn(Optional.of(new PersonalArtworkAccessResult(1L, 2L)));
    when(userExistenceRepository.existsById(2L)).thenReturn(true);
    when(personalArtworkQuestionRepository.findActiveByIdForUpdate(10L))
        .thenReturn(java.util.Optional.of(question));

    assertThatThrownBy(
            () -> service.deleteQuestion(new DeletePersonalArtworkQuestionCommand(1L, 10L, 2L)))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                org.assertj.core.api.Assertions.assertThat(exception.errorCode())
                    .isEqualTo(
                        PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_ALREADY_ANSWERED));

    verify(personalArtworkQuestionRepository, never()).save(any(PersonalArtworkQuestion.class));
  }
}
