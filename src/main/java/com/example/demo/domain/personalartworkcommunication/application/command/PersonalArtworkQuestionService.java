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
    personalArtworkQuestionValidator.validateContent(command.content());
    personalArtworkQuestionValidator.validateImages(command.images());

    PersonalArtworkQuestion personalArtworkQuestion =
        PersonalArtworkQuestion.create(
            command.personalArtworkId(),
            command.userId(),
            command.content(),
            command.isPublic(),
            command.images());

    PersonalArtworkQuestion savedQuestion =
        personalArtworkQuestionRepository.save(personalArtworkQuestion);

    return new PersonalArtworkQuestionResult(
        savedQuestion.getPersonalQuestionId(),
        savedQuestion.getContent(),
        savedQuestion.getIsPublic(),
        savedQuestion.getAnswerStatus(),
        savedQuestion.getCreatedAt(),
        savedQuestion.getUserId(),
        savedQuestion.getImages().stream()
            .map(
                image ->
                    new PersonalArtworkQuestionResult.ImageResult(
                        image.getPersonalQuestionImageId(),
                        image.getImageUrl(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getSortOrder()))
            .toList());
  }
}
