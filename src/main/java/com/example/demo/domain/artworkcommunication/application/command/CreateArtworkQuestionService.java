package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.DisplayArtworkExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateArtworkQuestionService {

    private final ArtworkQuestionRepository artworkQuestionRepository;
    private final DisplayArtworkExistenceRepository displayArtworkExistenceRepository;
    private final UserExistenceRepository userExistenceRepository;

    public ArtworkQuestionResult createQuestion(CreateArtworkQuestionCommand command) {
        validateDisplayArtworkExists(command.displayArtworkId());
        validateUserExists(command.userId());
        validateContent(command.content());

        ArtworkQuestion artworkQuestion =
                ArtworkQuestion.create(
                        command.displayArtworkId(),
                        command.userId(),
                        command.content(),
                        command.isPublic()
                );

        ArtworkQuestion savedQuestion =
                artworkQuestionRepository.save(artworkQuestion);

        return new ArtworkQuestionResult(
                savedQuestion.getArtQueId(),
                savedQuestion.getContent(),
                savedQuestion.getIsPublic(),
                savedQuestion.getAnswerStatus(),
                savedQuestion.getCreatedAt(),
                savedQuestion.getUpdatedAt(),
                savedQuestion.getDeletedAt(),
                savedQuestion.getDisplayArtworkId(),
                savedQuestion.getUserId()
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

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new BusinessException(ArtworkCommunicationErrorCode.INVALID_QUESTION_CONTENT);
        }
    }
}
