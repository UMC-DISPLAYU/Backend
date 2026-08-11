package com.example.demo.domain.personalartworkcommunication.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion.ImageInfo;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkExistenceRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonalArtworkQuestionServiceTest {

  @Mock private PersonalArtworkQuestionRepository personalArtworkQuestionRepository;
  @Mock private PersonalArtworkExistenceRepository personalArtworkExistenceRepository;
  @Mock private UserExistenceRepository userExistenceRepository;
  @Mock private PersonalArtworkQuestionReplyRepository personalArtworkQuestionReplyRepository;

  private PersonalArtworkQuestionService service;

  @BeforeEach
  void setUp() {
    PersonalArtworkQuestionValidator validator =
        new PersonalArtworkQuestionValidator(
            personalArtworkExistenceRepository,
            userExistenceRepository,
            personalArtworkQuestionReplyRepository,
            personalArtworkQuestionRepository);
    service = new PersonalArtworkQuestionService(personalArtworkQuestionRepository, validator);
  }

  @Test
  void ownerCanCreateQuestionOnOwnArtwork() {
    when(personalArtworkExistenceRepository.existsById(1L)).thenReturn(true);
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
    verify(personalArtworkExistenceRepository, never()).existsByIdAndUserId(any(), any());
    verify(personalArtworkQuestionRepository).save(any(PersonalArtworkQuestion.class));
  }
}
