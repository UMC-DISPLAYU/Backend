package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalArtworkFeelingReplyService {

  private final PersonalArtworkFeelingReplyRepository personalArtworkFeelingReplyRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final PersonalArtworkFeelingValidator personalArtworkFeelingValidator;

  public PersonalArtworkFeelingReplyResult createFeelingReply(
      PersonalArtworkFeelingReplyCommand command) {

    personalArtworkFeelingValidator.validatePersonalArtworkExists(command.personalArtworkId());
    personalArtworkFeelingValidator.validateUserExists(command.userId());
    personalArtworkFeelingValidator.validateContent(command.content());

    PersonalArtworkFeeling personalArtworkFeeling =
        personalArtworkFeelingValidator.findFeelingOrThrow(command.personalFeelingId());

    personalArtworkFeelingValidator.validateReplyTarget(
        personalArtworkFeeling, command.personalArtworkId());

    PersonalArtworkFeelingReply savedFeelingReply =
        personalArtworkFeelingReplyRepository.save(
            PersonalArtworkFeelingReply.create(
                command.personalFeelingId(), command.userId(), command.content()));

    String nickname =
        userExistenceRepository
            .findNicknameById(command.userId())
            .orElseThrow(
                () -> new BusinessException(PersonalArtworkCommunicationErrorCode.USER_NOT_FOUND));

    return new PersonalArtworkFeelingReplyResult(
        savedFeelingReply.getPersonalFeelingReplyId(),
        savedFeelingReply.getCreatedAt(),
        savedFeelingReply.getContent(),
        savedFeelingReply.getPersonalFeelingId(),
        savedFeelingReply.getUserId(),
        nickname);
  }
}
