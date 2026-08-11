package com.example.demo.domain.personalartworkcommunication.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeletePersonalArtworkFeelingReplyServiceTest {

  @Mock private PersonalArtworkFeelingReplyRepository personalArtworkFeelingReplyRepository;
  @Mock private PersonalArtworkFeelingValidator personalArtworkFeelingValidator;
  @InjectMocks private DeletePersonalArtworkFeelingReplyService service;

  @Test
  void replyCanBeDeletedAfterParentFeelingIsDeleted() {
    PersonalArtworkFeeling deletedFeeling =
        PersonalArtworkFeeling.create(1L, 2L, "삭제된 감상평", List.of());
    deletedFeeling.delete();
    PersonalArtworkFeelingReply reply =
        PersonalArtworkFeelingReply.create(10L, 3L, "답변", List.of());
    when(personalArtworkFeelingValidator.findFeelingOrThrow(10L)).thenReturn(deletedFeeling);
    when(personalArtworkFeelingValidator.findActiveReplyForUpdateOrThrow(100L)).thenReturn(reply);
    when(personalArtworkFeelingReplyRepository.save(reply)).thenReturn(reply);

    service.deleteReply(new DeletePersonalArtworkFeelingReplyCommand(1L, 10L, 100L, 3L));

    assertThat(reply.isDeleted()).isTrue();
    verify(personalArtworkFeelingValidator).validateReplyDeletionTarget(deletedFeeling, 1L);
    verify(personalArtworkFeelingValidator).validateAccessibleReply(reply, 10L, 3L);
    verify(personalArtworkFeelingReplyRepository).save(reply);
  }
}
