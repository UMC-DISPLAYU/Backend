package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingResult.ImageResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateArtworkFeelingService {

  private final ArtworkFeelingRepository artworkFeelingRepository;
  private final ArtworkFeelingValidator artworkFeelingValidator;

  @Transactional
  public ArtworkFeelingResult createFeeling(ArtworkFeelingCommand command) {
    artworkFeelingValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkFeelingValidator.validateUserExists(command.userId());
    artworkFeelingValidator.validateContent(command.content());
    artworkFeelingValidator.validateImages(command.images());

    ArtworkFeeling artworkFeeling =
        ArtworkFeeling.create(
            command.displayArtworkId(), command.userId(), command.content(), command.images());

    ArtworkFeeling savedFeeling = artworkFeelingRepository.save(artworkFeeling);

    return new ArtworkFeelingResult(
        savedFeeling.getFeelingId(),
        savedFeeling.getUserId(),
        savedFeeling.getContent(),
        savedFeeling.getCreatedAt(),
        savedFeeling.getImages().stream()
            .map(
                image ->
                    new ImageResult(
                        image.getFeelingImageId(),
                        image.getImageUrl(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getSortOrder()))
            .toList());
  }
}
