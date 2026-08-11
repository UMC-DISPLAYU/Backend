package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.permission.ArtworkCommunicationPermissionChecker;
import com.example.demo.domain.artworkcommunication.application.result.DeletedArtworkFeelingResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteArtworkFeelingService {

  private final ArtworkFeelingRepository artworkFeelingRepository;
  private final ArtworkFeelingValidator artworkFeelingValidator;
  private final ArtworkCommunicationPermissionChecker permissionChecker;

  @Transactional
  public DeletedArtworkFeelingResult deleteFeeling(DeleteArtworkFeelingCommand command) {
    artworkFeelingValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkFeelingValidator.validateUserExists(command.userId());

    ArtworkFeeling artworkFeeling =
        artworkFeelingRepository
            .findById(command.feelingId())
            .orElseThrow(
                () -> new BusinessException(ArtworkCommunicationErrorCode.FEELING_NOT_FOUND));

    artworkFeelingValidator.validateFeelingTarget(artworkFeeling, command.displayArtworkId());
    permissionChecker.requireFeelingWriter(artworkFeeling, command.userId());

    artworkFeeling.delete();
    ArtworkFeeling savedFeeling = artworkFeelingRepository.save(artworkFeeling);

    return new DeletedArtworkFeelingResult(
        savedFeeling.getFeelingId(), savedFeeling.getDeletedAt());
  }
}
