package com.example.demo.domain.personalartworkcommunication.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.personalartwork.application.result.PersonalArtworkAccessResult;
import com.example.demo.domain.personalartwork.application.usecase.GetPersonalArtworkAccessUseCase;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion.ImageInfo;
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
class PersonalArtworkQuestionServiceTest {

  @Mock private PersonalArtworkQuestionRepository personalArtworkQuestionRepository;
  @Mock private GetPersonalArtworkAccessUseCase getPersonalArtworkAccessUseCase;
  @Mock private UserExistenceRepository userExistenceRepository;
  @Mock private PersonalArtworkQuestionReplyRepository personalArtworkQuestionReplyRepository;

  private PersonalArtworkQuestionService service;

  @BeforeEach
  void setUp() {
    PersonalArtworkQuestionValidator validator =
        new PersonalArtworkQuestionValidator(
            getPersonalArtworkAccessUseCase,
            userExistenceRepository,
            personalArtworkQuestionReplyRepository,
            personalArtworkQuestionRepository);
    service = new PersonalArtworkQuestionService(personalArtworkQuestionRepository, validator);
  }

  @Test
  void ownerCanCreateQuestionOnOwnArtwork() {
    when(getPersonalArtworkAccessUseCase.getPersonalArtworkAccess(1L))
        .thenReturn(Optional.of(new PersonalArtworkAccessResult(1L, 2L)));
    when(userExistenceRepository.existsById(2L)).thenReturn(true);
    when(personalArtworkQuestionRepository.save(any(PersonalArtworkQuestion.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    PersonalArtworkQuestionResult result =
        service.createPersonalQuestion(
            new PersonalArtworkQuestionCommand(
                1L,
                2L,
                "작업자가 작성한 질문",
                true,
                List.of(new ImageInfo("https://image.test/question.jpg", 800, 600))));

    assertThat(result.userId()).isEqualTo(2L);
    assertThat(result.content()).isEqualTo("작업자가 작성한 질문");
    assertThat(result.images()).hasSize(1);
    assertThat(result.images().get(0).imageUrl()).isEqualTo("https://image.test/question.jpg");
    assertThat(result.images().get(0).width()).isEqualTo(800);
    assertThat(result.images().get(0).height()).isEqualTo(600);
    assertThat(result.images().get(0).sortOrder()).isZero();
    verify(personalArtworkQuestionRepository).save(any(PersonalArtworkQuestion.class));
  }

  @Test
  void emptyAccessResultIsTreatedAsPersonalArtworkNotFound() {
    when(getPersonalArtworkAccessUseCase.getPersonalArtworkAccess(99L))
        .thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                service.createPersonalQuestion(
                    new PersonalArtworkQuestionCommand(99L, 2L, "질문", true, List.of())))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_NOT_FOUND));

    verify(personalArtworkQuestionRepository, never()).save(any());
  }
}
