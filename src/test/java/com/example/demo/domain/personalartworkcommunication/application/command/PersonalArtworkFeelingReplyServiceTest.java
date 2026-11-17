package com.example.demo.domain.personalartworkcommunication.application.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
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
class PersonalArtworkFeelingReplyServiceTest {
  @Mock private PersonalArtworkFeelingReplyRepository replyRepository;
  @Mock private UserExistenceRepository users;
  @Mock private PersonalArtworkFeelingValidator validator;
  @InjectMocks private PersonalArtworkFeelingReplyService service;

  @Test
  void locksActiveParentBeforeCreatingReply() {
    PersonalArtworkFeeling feeling = PersonalArtworkFeeling.create(21L, 11L, "방명록", List.of());
    PersonalArtworkFeelingReply reply =
        PersonalArtworkFeelingReply.create(31L, 12L, "답글", List.of());
    when(validator.findActiveFeelingForUpdateOrThrow(31L)).thenReturn(feeling);
    when(replyRepository.save(any())).thenReturn(reply);
    when(users.findNicknameById(12L)).thenReturn(Optional.of("사용자"));

    service.createFeelingReply(
        new PersonalArtworkFeelingReplyCommand(21L, 31L, 12L, "답글", List.of()));

    InOrder order = Mockito.inOrder(validator, replyRepository);
    order.verify(validator).findActiveFeelingForUpdateOrThrow(31L);
    order.verify(validator).validateReplyTarget(feeling, 21L);
    order.verify(replyRepository).save(any());
  }
}
