package com.example.demo.domain.artworkcommunication.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.artworkcommunication.application.permission.ArtworkCommunicationPermissionChecker;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.DisplayArtworkExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteArtworkQuestionServiceTest {

  @Mock private ArtworkQuestionRepository artworkQuestionRepository;
  @Mock private DisplayArtworkExistenceRepository displayArtworkExistenceRepository;
  @Mock private UserExistenceRepository userExistenceRepository;
  @Mock private CreatorExistenceRepository creatorExistenceRepository;
  @Mock private ArtworkQuestionReplyRepository artworkQuestionReplyRepository;

  private DeleteArtworkQuestionService service;

  @BeforeEach
  void setUp() {
    ArtworkQuestionValidator validator =
        new ArtworkQuestionValidator(
            artworkQuestionRepository,
            displayArtworkExistenceRepository,
            userExistenceRepository,
            artworkQuestionReplyRepository);
    service =
        new DeleteArtworkQuestionService(
            artworkQuestionRepository,
            validator,
            new ArtworkCommunicationPermissionChecker(creatorExistenceRepository));
  }

  @Test
  void answeredQuestionCannotBeDeleted() {
    ArtworkQuestion question = ArtworkQuestion.create(1L, 2L, "답변이 등록된 질문", true, List.of());
    question.markAnswered();
    when(displayArtworkExistenceRepository.existsById(1L)).thenReturn(true);
    when(userExistenceRepository.existsById(2L)).thenReturn(true);
    when(artworkQuestionRepository.findActiveByIdForUpdate(10L)).thenReturn(Optional.of(question));

    assertThatThrownBy(() -> service.deleteQuestion(new DeleteArtworkQuestionCommand(1L, 10L, 2L)))
        .isInstanceOfSatisfying(
            BusinessException.class,
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArtworkCommunicationErrorCode.QUESTION_ALREADY_ANSWERED));

    verify(artworkQuestionRepository, never()).save(any(ArtworkQuestion.class));
  }
}
