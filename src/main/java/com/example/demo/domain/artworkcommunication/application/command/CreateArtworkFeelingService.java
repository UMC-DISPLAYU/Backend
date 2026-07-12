package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.DisplayArtworkExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateArtworkFeelingService {

  private final ArtworkFeelingRepository artworkFeelingRepository;
  private final DisplayArtworkExistenceRepository displayArtworkExistenceRepository;
  private final UserExistenceRepository userExistenceRepository;

  public ArtworkFeelingResult createFeeling(ArtworkFeelingCommand command) {
    validateDisplayArtworkExists(command.displayArtworkId());
    validateUserExists(command.userId());

    ArtworkFeeling artworkFeeling =
        ArtworkFeeling.create(command.displayArtworkId(), command.userId(), command.content());

    ArtworkFeeling savedFeeling = artworkFeelingRepository.save(artworkFeeling);

    return new ArtworkFeelingResult(
        savedFeeling.getFeelingId(),
        savedFeeling.getUserId(),
        savedFeeling.getContent(),
        savedFeeling.getCreatedAt());
  }

  private void validateDisplayArtworkExists(Long displayArtworkId) {
    if (!displayArtworkExistenceRepository.existsById(displayArtworkId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_NOT_FOUND);
    }
  }

  private void validateUserExists(Long userId) {
    if (!userExistenceRepository.existsById(userId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.USER_NOT_FOUND);
    }
  }
}
