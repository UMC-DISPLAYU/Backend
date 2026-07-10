package com.example.demo.domain.artworkcommunication.presentation;

import com.example.demo.domain.artworkcommunication.application.command.ArtworkFeelingCommand;
import com.example.demo.domain.artworkcommunication.application.command.CreateArtworkFeelingService;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingResult;
import com.example.demo.domain.artworkcommunication.presentation.mapper.ArtworkFeelingPresentationMapper;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/artworks/{artworkId}/feelings")
public class ArtworkFeelingController {

    private final CreateArtworkFeelingService createArtworkFeelingService;
    private final ArtworkFeelingPresentationMapper mapper;

    @PostMapping
    public ApiResponseBody<ArtworkFeelingResponse> createFeeling(
            @PathVariable Long artworkId,
            @RequestHeader("X-User-Id") Long userId, //테스트용
            @Valid @RequestBody CreateArtworkFeelingRequest request,
            HttpServletRequest httpServletRequest
    ) {
        ArtworkFeelingCommand command =
                mapper.toCommand(artworkId, userId, request);

        ArtworkFeelingResult result =
                createArtworkFeelingService.createFeeling(command);

        ArtworkFeelingResponse response =
                mapper.toResponse(result);

        return ApiResponseBody.success(response, httpServletRequest);
    }
}
