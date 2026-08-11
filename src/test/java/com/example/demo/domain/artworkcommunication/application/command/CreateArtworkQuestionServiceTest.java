package com.example.demo.domain.artworkcommunication.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.DisplayArtworkExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateArtworkQuestionServiceTest {

  @Mock private ArtworkQuestionRepository artworkQuestionRepository;
  @Mock private DisplayArtworkExistenceRepository displayArtworkExistenceRepository;
  @Mock private UserExistenceRepository userExistenceRepository;
  @Mock private ArtworkQuestionReplyRepository artworkQuestionReplyRepository;

  private CreateArtworkQuestionService service;

  @BeforeEach
  void setUp() {
    ArtworkQuestionValidator validator =
        new ArtworkQuestionValidator(
            artworkQuestionRepository,
            displayArtworkExistenceRepository,
            userExistenceRepository,
            artworkQuestionReplyRepository);
    service = new CreateArtworkQuestionService(artworkQuestionRepository, validator);
  }

  @Test
  void creatorCanCreateQuestionOnOwnArtwork() {
    when(displayArtworkExistenceRepository.existsById(1L)).thenReturn(true);
    when(userExistenceRepository.existsById(2L)).thenReturn(true);
    when(artworkQuestionRepository.save(any(ArtworkQuestion.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    ArtworkQuestionResult result =
        service.createQuestion(new CreateArtworkQuestionCommand(1L, 2L, "작가가 작성한 질문", true));

    assertThat(result.displayArtworkId()).isEqualTo(1L);
    assertThat(result.userId()).isEqualTo(2L);
    assertThat(result.content()).isEqualTo("작가가 작성한 질문");
    verify(artworkQuestionRepository).save(any(ArtworkQuestion.class));
  }
}
