package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.UpdatedArtworkFeelingResult;
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
public class UpdateArtworkFeelingService {

    private final ArtworkFeelingRepository artworkFeelingRepository;
    private final DisplayArtworkExistenceRepository displayArtworkExistenceRepository;
    private final UserExistenceRepository userExistenceRepository;

    public UpdatedArtworkFeelingResult updateFeeling(UpdateArtworkFeelingCommand command) {
        validateDisplayArtworkExists(command.displayArtworkId());
        validateUserExists(command.userId());

        ArtworkFeeling artworkFeeling =
                artworkFeelingRepository.findById(command.feelingId())
                        .orElseThrow(() ->
                                new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_FEELING_NOT_FOUND));

        validateArtworkFeelingBelongsToArtwork(artworkFeeling, command.displayArtworkId());
        validateWriter(artworkFeeling, command.userId());

        artworkFeeling.updateContent(command.content());

        ArtworkFeeling savedFeeling =
                artworkFeelingRepository.save(artworkFeeling);

        return new UpdatedArtworkFeelingResult(
                savedFeeling.getFeelingId(),
                savedFeeling.getContent(),
                savedFeeling.getUpdatedAt()
        );
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

    private void validateArtworkFeelingBelongsToArtwork(
            ArtworkFeeling artworkFeeling,
            Long displayArtworkId
    ) {
        if (!artworkFeeling.belongsToArtwork(displayArtworkId)) {
            throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_FEELING_NOT_FOUND);
        }
    }

    private void validateWriter(ArtworkFeeling artworkFeeling, Long userId) {
        if (!artworkFeeling.isWrittenBy(userId)) {
            throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_FEELING_FORBIDDEN);
        }
    }
}
