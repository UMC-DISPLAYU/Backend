package com.example.demo.domain.personalartworkcommunication.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkQuestionValidator;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionListResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkExistenceRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyLikeRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.domain.personalartworkcommunication.domain.type.AnswerStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetPersonalArtworkQuestionsServiceTest {

  @Mock private PersonalArtworkQuestionRepository personalArtworkQuestionRepository;
  @Mock private PersonalArtworkQuestionReplyRepository personalArtworkQuestionReplyRepository;
  @Mock private PersonalArtworkQuestionLikeRepository personalArtworkQuestionLikeRepository;

  @Mock
  private PersonalArtworkQuestionReplyLikeRepository personalArtworkQuestionReplyLikeRepository;

  @Mock private PersonalArtworkExistenceRepository personalArtworkExistenceRepository;
  @Mock private UserExistenceRepository userExistenceRepository;

  private GetPersonalArtworkQuestionsService service;

  @BeforeEach
  void setUp() {
    PersonalArtworkQuestionValidator validator =
        new PersonalArtworkQuestionValidator(
            personalArtworkExistenceRepository,
            userExistenceRepository,
            personalArtworkQuestionReplyRepository,
            personalArtworkQuestionRepository);
    service =
        new GetPersonalArtworkQuestionsService(
            personalArtworkQuestionRepository,
            personalArtworkQuestionReplyRepository,
            personalArtworkQuestionLikeRepository,
            personalArtworkQuestionReplyLikeRepository,
            personalArtworkExistenceRepository,
            userExistenceRepository,
            validator);
  }

  @Test
  void ownerQuestionIsMarkedAsCreatorAndLikedForLoggedInOwner() {
    PersonalArtworkQuestion question = mock(PersonalArtworkQuestion.class);
    when(question.getPersonalQuestionId()).thenReturn(10L);
    when(question.getContent()).thenReturn("작품 소유자가 작성한 질문");
    when(question.getIsPublic()).thenReturn(true);
    when(question.getAnswerStatus()).thenReturn(AnswerStatus.WAITING);
    when(question.getCreatedAt()).thenReturn(LocalDateTime.of(2026, 8, 9, 12, 0));
    when(question.getUserId()).thenReturn(2L);

    when(personalArtworkExistenceRepository.existsById(1L)).thenReturn(true);
    when(personalArtworkExistenceRepository.findOwnerUserIdById(1L)).thenReturn(Optional.of(2L));
    when(personalArtworkQuestionRepository.findActiveByPersonalArtworkIdWithCursor(1L, null, 11))
        .thenReturn(List.of(question));
    when(personalArtworkQuestionReplyRepository.findActiveByPersonalQuestionIds(List.of(10L)))
        .thenReturn(List.of());
    when(userExistenceRepository.findNicknamesByIds(Set.of(2L))).thenReturn(Map.of(2L, "작품 소유자"));
    when(personalArtworkQuestionLikeRepository.countByPersonalQuestionIds(List.of(10L)))
        .thenReturn(Map.of(10L, 1L));
    when(personalArtworkQuestionLikeRepository.findLikedPersonalQuestionIds(List.of(10L), 2L))
        .thenReturn(Set.of(10L));
    when(personalArtworkQuestionReplyLikeRepository.countByPersonalQuestionReplyIds(List.of()))
        .thenReturn(Map.of());
    when(personalArtworkQuestionReplyLikeRepository.findLikedPersonalQuestionReplyIds(
            List.of(), 2L))
        .thenReturn(Set.of());

    PersonalArtworkQuestionListResult result =
        service.getQuestions(new GetPersonalArtworkQuestionsQuery(1L, null, 10, 2L));

    assertThat(result.size()).isEqualTo(10);
    assertThat(result.questions()).hasSize(1);
    assertThat(result.questions().get(0).user().isCreator()).isTrue();
    assertThat(result.questions().get(0).isLiked()).isTrue();
    assertThat(result.questions().get(0).canReply()).isTrue();
  }

  @Test
  void pageSizeIsLimitedToFifty() {
    when(personalArtworkExistenceRepository.existsById(1L)).thenReturn(true);
    when(personalArtworkExistenceRepository.findOwnerUserIdById(1L)).thenReturn(Optional.of(2L));
    when(personalArtworkQuestionRepository.findActiveByPersonalArtworkIdWithCursor(1L, null, 51))
        .thenReturn(List.of());

    PersonalArtworkQuestionListResult result =
        service.getQuestions(new GetPersonalArtworkQuestionsQuery(1L, null, 100, null));

    assertThat(result.size()).isEqualTo(50);
    assertThat(result.questions()).isEmpty();
  }
}
