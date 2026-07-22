package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkFeelingResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeletePersonalArtworkFeelingService {

  private final PersonalArtworkFeelingValidator personalArtworkFeelingValidator;
  private final PersonalArtworkFeelingRepository personalArtworkFeelingRepository;

  public DeletedPersonalArtworkFeelingResult deleteFeeling(
      DeletePersonalArtworkFeelingCommand command) {
    personalArtworkFeelingValidator.validatePersonalArtworkExists(command.personalArtworkId());
    personalArtworkFeelingValidator.validateUserExists(command.userId());

    PersonalArtworkFeeling personalArtworkFeeling =
        personalArtworkFeelingRepository
            .findById(command.personalFeelingId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode.PERSONAL_FEELING_NOT_FOUND));

    personalArtworkFeelingValidator.validateAccessiblePersonalFeeling(
        personalArtworkFeeling, command.personalArtworkId(), command.userId());

    personalArtworkFeeling.delete();
    PersonalArtworkFeeling savedFeeling =
        personalArtworkFeelingRepository.save(personalArtworkFeeling);

    return new DeletedPersonalArtworkFeelingResult(
        savedFeeling.getPersonalFeelingId(), savedFeeling.getDeletedAt());
  }
}
