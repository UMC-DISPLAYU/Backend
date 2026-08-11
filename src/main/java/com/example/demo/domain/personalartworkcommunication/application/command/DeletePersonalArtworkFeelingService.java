package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.permission.PersonalArtworkCommunicationPermissionChecker;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkFeelingResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeletePersonalArtworkFeelingService {

  private final PersonalArtworkFeelingValidator personalArtworkFeelingValidator;
  private final PersonalArtworkCommunicationPermissionChecker permissionChecker;
  private final PersonalArtworkFeelingRepository personalArtworkFeelingRepository;

  public DeletedPersonalArtworkFeelingResult deleteFeeling(
      DeletePersonalArtworkFeelingCommand command) {
    personalArtworkFeelingValidator.validatePersonalArtworkExists(command.personalArtworkId());
    personalArtworkFeelingValidator.validateUserExists(command.userId());

    PersonalArtworkFeeling personalArtworkFeeling =
        personalArtworkFeelingValidator.findFeelingOrThrow(command.personalFeelingId());

    personalArtworkFeelingValidator.validateFeelingTarget(
        personalArtworkFeeling, command.personalArtworkId());
    permissionChecker.requirePersonalFeelingWriter(personalArtworkFeeling, command.userId());

    personalArtworkFeeling.delete();
    PersonalArtworkFeeling savedFeeling =
        personalArtworkFeelingRepository.save(personalArtworkFeeling);

    return new DeletedPersonalArtworkFeelingResult(
        savedFeeling.getPersonalFeelingId(), savedFeeling.getDeletedAt());
  }
}
