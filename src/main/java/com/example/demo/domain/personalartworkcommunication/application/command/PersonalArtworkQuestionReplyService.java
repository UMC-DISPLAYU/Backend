package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionReplyResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkExistenceRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalArtworkQuestionReplyService {

  private final PersonalArtworkQuestionRepository personalArtworkQuestionRepository;
  private final PersonalArtworkExistenceRepository personalArtworkExistenceRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final PersonalArtworkQuestionReplyRepository personalArtworkQuestionReplyRepository;
  private final PersonalArtworkQuestionValidator personalArtworkQuestionValidator;

  public PersonalArtworkQuestionReplyResult createQuestionReply(
      PersonalArtworkQuestionReplyCommand command) {

    personalArtworkQuestionValidator.validatePersonalArtworkExists(command.personalArtworkId());
    personalArtworkQuestionValidator.validateUserExists(command.userId());
    personalArtworkQuestionValidator.validateContent(command.content());
    personalArtworkQuestionValidator.validateReplyImages(command.images());
    personalArtworkQuestionValidator.validatePersonalArtworkCreator(
        command.personalArtworkId(), command.userId());

    PersonalArtworkQuestion personalArtworkQuestion =
        personalArtworkQuestionRepository
            .findActiveById(command.personalQuestionId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_NOT_FOUND));

    personalArtworkQuestionValidator.validateQuestionTarget(
        personalArtworkQuestion, command.personalArtworkId());

    PersonalArtworkQuestionReply questionReply =
        personalArtworkQuestion.answer(command.userId(), command.content(), command.images());
    PersonalArtworkQuestionReply savedQuestionReply = saveQuestionReplyOrThrow(questionReply);

    boolean isCreator =
        personalArtworkExistenceRepository.existsByIdAndUserId(
            command.personalArtworkId(), command.userId());

    String nickname =
        userExistenceRepository
            .findNicknameById(command.userId())
            .orElseThrow(
                () -> new BusinessException(PersonalArtworkCommunicationErrorCode.USER_NOT_FOUND));

    return new PersonalArtworkQuestionReplyResult(
        savedQuestionReply.getPersonalQuestionReplyId(),
        savedQuestionReply.getCreatedAt(),
        savedQuestionReply.getContent(),
        savedQuestionReply.getPersonalQuestionId(),
        savedQuestionReply.getUserId(),
        nickname,
        isCreator,
        savedQuestionReply.getImages().stream()
            .map(
                image ->
                    new PersonalArtworkQuestionReplyResult.ImageResult(
                        image.getPersonalQuestionReplyImageId(),
                        image.getImageUrl(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getSortOrder()))
            .toList());
  }

  private PersonalArtworkQuestionReply saveQuestionReplyOrThrow(
      PersonalArtworkQuestionReply questionReply) {
    try {
      return personalArtworkQuestionReplyRepository.save(questionReply);
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_ALREADY_ANSWERED);
    }
  }
}
