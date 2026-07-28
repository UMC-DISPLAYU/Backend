package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateArtworkQuestionService {

  private final ArtworkQuestionRepository artworkQuestionRepository;
  private final ArtworkQuestionValidator artworkQuestionValidator;

  public ArtworkQuestionResult createQuestion(CreateArtworkQuestionCommand command) {
    artworkQuestionValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkQuestionValidator.validateUserExists(command.userId());
    artworkQuestionValidator.validateNotArtworkCreator(
        command.displayArtworkId(), command.userId());
    artworkQuestionValidator.validateContent(command.content());

    ArtworkQuestion artworkQuestion =
        ArtworkQuestion.create(
            command.displayArtworkId(), command.userId(), command.content(), command.isPublic());

    ArtworkQuestion savedQuestion = artworkQuestionRepository.save(artworkQuestion);

    return new ArtworkQuestionResult(
        savedQuestion.getQuestionId(),
        savedQuestion.getContent(),
        savedQuestion.getIsPublic(),
        savedQuestion.getAnswerStatus(),
        savedQuestion.getCreatedAt(),
        savedQuestion.getDisplayArtworkId(),
        savedQuestion.getUserId());
  }
}
