package com.example.demo.domain.artworkcommunication.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.artworkcommunication.application.command.ArtworkQuestionValidator;
import com.example.demo.domain.artworkcommunication.application.permission.ArtworkCommunicationPermissionChecker;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionListResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.DisplayArtworkExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
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
class GetArtworkQuestionsServiceTest {

  @Mock private DisplayArtworkExistenceRepository displayArtworkExistenceRepository;
  @Mock private ArtworkQuestionRepository artworkQuestionRepository;
  @Mock private ArtworkQuestionReplyRepository artworkQuestionReplyRepository;
  @Mock private UserExistenceRepository userExistenceRepository;
  @Mock private CreatorExistenceRepository creatorExistenceRepository;

  private GetArtworkQuestionsService service;

  @BeforeEach
  void setUp() {
    ArtworkQuestionValidator validator =
        new ArtworkQuestionValidator(
            artworkQuestionRepository,
            displayArtworkExistenceRepository,
            userExistenceRepository,
            artworkQuestionReplyRepository);
    service =
        new GetArtworkQuestionsService(
            displayArtworkExistenceRepository,
            artworkQuestionRepository,
            artworkQuestionReplyRepository,
            userExistenceRepository,
            creatorExistenceRepository,
            validator,
            new ArtworkCommunicationPermissionChecker(creatorExistenceRepository));
  }

  @Test
  void questionWriterIsMarkedAsCreatorWhenParticipatingInArtwork() {
    ArtworkQuestion question = mock(ArtworkQuestion.class);
    when(question.getQuestionId()).thenReturn(10L);
    when(question.getContent()).thenReturn("작가가 작성한 질문");
    when(question.getIsPublic()).thenReturn(true);
    when(question.getAnswerStatus()).thenReturn(AnswerStatus.WAITING);
    when(question.getCreatedAt()).thenReturn(LocalDateTime.of(2026, 8, 9, 12, 0));
    when(question.getUserId()).thenReturn(2L);

    when(displayArtworkExistenceRepository.existsById(1L)).thenReturn(true);
    when(artworkQuestionRepository.findActiveByDisplayArtworkIdWithCursor(1L, null, 11))
        .thenReturn(List.of(question));
    when(artworkQuestionReplyRepository.findActiveByQuestionIds(List.of(10L)))
        .thenReturn(List.of());
    when(userExistenceRepository.findNicknamesByIds(Set.of(2L))).thenReturn(Map.of(2L, "작가 닉네임"));
    when(creatorExistenceRepository.findCreatorNamesByDisplayArtworkIdAndUserIds(1L, Set.of(2L)))
        .thenReturn(Map.of(2L, "작가명"));
    when(creatorExistenceRepository.findCreatorNamesByIds(Set.of())).thenReturn(Map.of());

    ArtworkQuestionListResult result =
        service.getQuestions(new GetArtworkQuestionsQuery(1L, null, 10, null));

    assertThat(result.questions()).hasSize(1);
    assertThat(result.size()).isEqualTo(10);
    assertThat(result.questions().get(0).user().isCreator()).isTrue();
    assertThat(result.questions().get(0).isLiked()).isFalse();
  }

  @Test
  void loggedInUserReceivesQuestionReplyWithoutLikeStatus() {
    ArtworkQuestion question = mock(ArtworkQuestion.class);
    ArtworkQuestionReply reply = mock(ArtworkQuestionReply.class);
    when(question.getQuestionId()).thenReturn(10L);
    when(question.getContent()).thenReturn("좋아요한 질문");
    when(question.getIsPublic()).thenReturn(true);
    when(question.getAnswerStatus()).thenReturn(AnswerStatus.WAITING);
    when(question.getCreatedAt()).thenReturn(LocalDateTime.of(2026, 8, 9, 12, 0));
    when(question.getUserId()).thenReturn(2L);
    when(reply.getQueReplyId()).thenReturn(20L);
    when(reply.getQuestionId()).thenReturn(10L);
    when(reply.getCreatorId()).thenReturn(4L);
    when(reply.getContent()).thenReturn("답변");
    when(reply.getCreatedAt()).thenReturn(LocalDateTime.of(2026, 8, 9, 13, 0));

    when(displayArtworkExistenceRepository.existsById(1L)).thenReturn(true);
    when(creatorExistenceRepository.findParticipantNameByDisplayArtworkIdAndUserId(1L, 2L))
        .thenReturn(Optional.of("답변 작가"));
    when(creatorExistenceRepository.findContactCreatorByDisplayArtworkIdAndUserId(1L, 2L))
        .thenReturn(Optional.of(new CreatorExistenceRepository.ContactCreator(4L, "답변 작가")));
    when(artworkQuestionRepository.findActiveByDisplayArtworkIdWithCursor(1L, null, 11))
        .thenReturn(List.of(question));
    when(artworkQuestionReplyRepository.findActiveByQuestionIds(List.of(10L)))
        .thenReturn(List.of(reply));
    when(userExistenceRepository.findNicknamesByIds(Set.of(2L))).thenReturn(Map.of(2L, "질문자"));
    when(creatorExistenceRepository.findCreatorNamesByDisplayArtworkIdAndUserIds(1L, Set.of(2L)))
        .thenReturn(Map.of(2L, "답변 작가"));
    when(creatorExistenceRepository.findCreatorNamesByIds(Set.of(4L)))
        .thenReturn(Map.of(4L, "답변 작가"));

    ArtworkQuestionListResult result =
        service.getQuestions(new GetArtworkQuestionsQuery(1L, null, 10, 2L));

    assertThat(result.questions().get(0).isMine()).isTrue();
    assertThat(result.questions().get(0).user().isCreator()).isTrue();
    assertThat(result.questions().get(0).isLiked()).isFalse();
    assertThat(result.questions().get(0).likeCount()).isZero();
    assertThat(result.questions().get(0).reply().likeCount()).isZero();
    assertThat(result.questions().get(0).reply().isLiked()).isFalse();
    assertThat(result.questions().get(0).reply().isMine()).isTrue();
  }

  @Test
  void pageSizeIsLimitedToFifty() {
    when(displayArtworkExistenceRepository.existsById(1L)).thenReturn(true);
    when(artworkQuestionRepository.findActiveByDisplayArtworkIdWithCursor(1L, null, 51))
        .thenReturn(List.of());

    ArtworkQuestionListResult result =
        service.getQuestions(new GetArtworkQuestionsQuery(1L, null, 100, null));

    assertThat(result.size()).isEqualTo(50);
    assertThat(result.questions()).isEmpty();
  }
}
