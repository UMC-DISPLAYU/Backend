package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalArtworkQuestionService {

  private final PersonalArtworkQuestionRepository personalArtworkQuestionRepository;
  private final PersonalArtworkQuestionValidator personalArtworkQuestionValidator;

  public PersonalArtworkQuestionResult createPersonalQuestion(
      PersonalArtworkQuestionCommand command) {
    personalArtworkQuestionValidator.validatePersonalArtworkExists(command.personalArtworkId());
    personalArtworkQuestionValidator.validateUserExists(command.userId());
    personalArtworkQuestionValidator.validateNotPersonalArtworkCreator(
        command.personalArtworkId(), command.userId());
    personalArtworkQuestionValidator.validateContent(command.content());

    PersonalArtworkQuestion personalArtworkQuestion =
        PersonalArtworkQuestion.create(
            command.personalArtworkId(), command.userId(), command.content(), command.isPublic());

    PersonalArtworkQuestion savedQuestion =
        personalArtworkQuestionRepository.save(personalArtworkQuestion);

    return new PersonalArtworkQuestionResult(
        savedQuestion.getPersonalQuestionId(),
        savedQuestion.getContent(),
        savedQuestion.getIsPublic(),
        savedQuestion.getAnswerStatus(),
        savedQuestion.getCreatedAt(),
        savedQuestion.getUserId());
  }
}
