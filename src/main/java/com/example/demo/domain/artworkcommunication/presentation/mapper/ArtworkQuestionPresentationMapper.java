package com.example.demo.domain.artworkcommunication.presentation.mapper;

import com.example.demo.domain.artworkcommunication.application.command.CreateArtworkQuestionCommand;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionResult;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkQuestionRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkQuestionResponse;
import org.springframework.stereotype.Component;

@Component
public class ArtworkQuestionPresentationMapper {

    public CreateArtworkQuestionCommand toCommand(
            Long artworkId,
            Long userId,
            CreateArtworkQuestionRequest request
    ) {
        return new CreateArtworkQuestionCommand(
                artworkId,
                userId,
                request.content(),
                request.isPublic()
        );
    }

    public ArtworkQuestionResponse toResponse(ArtworkQuestionResult result) {
        return new ArtworkQuestionResponse(
                result.artQueId(),
                result.content(),
                result.isPublic(),
                result.answerStatus(),
                result.createdAt(),
                result.updatedAt(),
                result.deletedAt(),
                result.displayArtworkId(),
                result.userId()
        );
    }
}
