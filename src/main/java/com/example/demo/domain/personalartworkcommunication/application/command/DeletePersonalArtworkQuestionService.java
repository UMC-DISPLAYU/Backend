package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.permission.PersonalArtworkCommunicationPermissionChecker;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkQuestionResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionRepository;
import com.example.demo.global.error.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class DeletePersonalArtworkQuestionService {

  private final PersonalArtworkQuestionValidator personalArtworkQuestionValidator;
  private final PersonalArtworkCommunicationPermissionChecker permissionChecker;
  private final PersonalArtworkQuestionRepository personalArtworkQuestionRepository;

  public DeletedPersonalArtworkQuestionResult deleteQuestion(
      DeletePersonalArtworkQuestionCommand command) {
    personalArtworkQuestionValidator.validatePersonalArtworkExists(command.personalArtworkId());
    personalArtworkQuestionValidator.validateUserExists(command.userId());

    PersonalArtworkQuestion personalArtworkQuestion =
        personalArtworkQuestionRepository
            .findById(command.personalQuestionId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_NOT_FOUND));

    personalArtworkQuestionValidator.validateQuestionTarget(
        personalArtworkQuestion, command.personalArtworkId());
    permissionChecker.requirePersonalQuestionWriter(personalArtworkQuestion, command.userId());

    personalArtworkQuestion.delete();
    PersonalArtworkQuestion savedQuestion =
        personalArtworkQuestionRepository.save(personalArtworkQuestion);

    return new DeletedPersonalArtworkQuestionResult(
        savedQuestion.getPersonalQuestionId(), savedQuestion.getDeletedAt());
  }
}
