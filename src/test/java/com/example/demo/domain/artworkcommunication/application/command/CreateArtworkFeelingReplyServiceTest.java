package com.example.demo.domain.artworkcommunication.application.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateArtworkFeelingReplyServiceTest {
  @Mock private ArtworkFeelingReplyRepository replyRepository;
  @Mock private ArtworkFeelingRepository feelingRepository;
  @Mock private UserExistenceRepository users;
  @Mock private CreatorExistenceRepository creators;
  @Mock private ArtworkFeelingValidator validator;
  @InjectMocks private CreateArtworkFeelingReplyService service;

  @Test
  void locksActiveParentBeforeCreatingReply() {
    ArtworkFeeling feeling = ArtworkFeeling.create(21L, 11L, "방명록", List.of());
    ArtworkFeelingReply reply = ArtworkFeelingReply.create(31L, 12L, "답글", List.of());
    when(feelingRepository.findActiveByIdForUpdate(31L)).thenReturn(Optional.of(feeling));
    when(replyRepository.save(any())).thenReturn(reply);
    when(creators.findParticipantNameByDisplayArtworkIdAndUserId(21L, 12L))
        .thenReturn(Optional.empty());
    when(users.findNicknameById(12L)).thenReturn(Optional.of("사용자"));

    service.createFeelingReply(new ArtworkFeelingReplyCommand(21L, 31L, 12L, "답글", List.of()));

    InOrder order = Mockito.inOrder(feelingRepository, validator, replyRepository);
    order.verify(feelingRepository).findActiveByIdForUpdate(31L);
    order.verify(validator).validateReplyTarget(feeling, 21L);
    order.verify(replyRepository).save(any());
  }
}
