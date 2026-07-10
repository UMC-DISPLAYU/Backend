package com.example.demo.domain.artworkcommunication.presentation.mapper;

import com.example.demo.domain.artworkcommunication.application.command.ArtworkFeelingCommand;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingResult;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingResponse;
import org.springframework.stereotype.Component;

@Component
public class ArtworkFeelingPresentationMapper {

    public ArtworkFeelingCommand toCommand(
            Long artworkId,
            Long userId,
            CreateArtworkFeelingRequest request
    ) {
        return new ArtworkFeelingCommand(artworkId, userId, request.content());
    }

    public ArtworkFeelingResponse toResponse(ArtworkFeelingResult result) {
        return new ArtworkFeelingResponse(
                result.feelingId(),
                result.userId(),
                result.content(),
                result.createdAt()
        );
    }
}
