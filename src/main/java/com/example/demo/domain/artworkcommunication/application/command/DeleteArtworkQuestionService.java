package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.DeletedArtworkQuestionResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteArtworkQuestionService {

  private final ArtworkQuestionRepository artworkQuestionRepository;
  private final ArtworkQuestionValidator artworkQuestionValidator;

  public DeletedArtworkQuestionResult deleteQuestion(DeleteArtworkQuestionCommand command) {
    artworkQuestionValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkQuestionValidator.validateUserExists(command.userId());

    ArtworkQuestion artworkQuestion =
        artworkQuestionValidator.findQuestionOrThrow(command.questionId());

    artworkQuestionValidator.validateQuestionTarget(artworkQuestion, command.displayArtworkId());
    artworkQuestionValidator.validateWriter(artworkQuestion, command.userId());

    artworkQuestion.delete();
    ArtworkQuestion savedQuestion = artworkQuestionRepository.save(artworkQuestion);

    return new DeletedArtworkQuestionResult(
        savedQuestion.getArtQueId(), savedQuestion.getDeletedAt());
  }
}
