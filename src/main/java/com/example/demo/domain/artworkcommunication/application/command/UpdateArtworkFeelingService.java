package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.UpdatedArtworkFeelingResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateArtworkFeelingService {

  private final ArtworkFeelingRepository artworkFeelingRepository;
  private final ArtworkFeelingValidator artworkFeelingValidator;

  public UpdatedArtworkFeelingResult updateFeeling(UpdateArtworkFeelingCommand command) {
    artworkFeelingValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkFeelingValidator.validateUserExists(command.userId());
    artworkFeelingValidator.validateContent(command.content());

    ArtworkFeeling artworkFeeling =
        artworkFeelingRepository
            .findById(command.feelingId())
            .orElseThrow(
                () -> new BusinessException(ArtworkCommunicationErrorCode.FEELING_NOT_FOUND));

    artworkFeelingValidator.validateAccessibleFeeling(
        artworkFeeling, command.displayArtworkId(), command.userId());

    artworkFeeling.updateContent(command.content());

    ArtworkFeeling savedFeeling = artworkFeelingRepository.save(artworkFeeling);

    return new UpdatedArtworkFeelingResult(
        savedFeeling.getFeelingId(), savedFeeling.getContent(), savedFeeling.getUpdatedAt());
  }
}
