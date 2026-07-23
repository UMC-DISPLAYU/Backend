package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalArtworkFeelingService {

  private final PersonalArtworkFeelingRepository personalArtworkFeelingRepository;
  private final PersonalArtworkFeelingValidator personalArtworkFeelingValidator;

  public PersonalArtworkFeelingResult createPersonalFeeling(PersonalArtworkFeelingCommand command) {
    personalArtworkFeelingValidator.validatePersonalArtworkExists(command.personalArtworkId());
    personalArtworkFeelingValidator.validateUserExists(command.userId());
    personalArtworkFeelingValidator.validateContent(command.content());
    personalArtworkFeelingValidator.validateNotPersonalArtworkCreator(
        command.personalArtworkId(), command.userId());

    PersonalArtworkFeeling personalArtworkFeeling =
        PersonalArtworkFeeling.create(
            command.personalArtworkId(), command.userId(), command.content());

    PersonalArtworkFeeling savedFeeling =
        personalArtworkFeelingRepository.save(personalArtworkFeeling);

    return new PersonalArtworkFeelingResult(
        savedFeeling.getPersonalFeelingId(),
        savedFeeling.getUserId(),
        savedFeeling.getContent(),
        savedFeeling.getCreatedAt());
  }
}
