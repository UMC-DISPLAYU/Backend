package com.example.demo.domain.artworkcommunication.presentation;

import com.example.demo.domain.artworkcommunication.application.command.ArtworkFeelingCommand;
import com.example.demo.domain.artworkcommunication.application.command.CreateArtworkFeelingService;
import com.example.demo.domain.artworkcommunication.application.command.DeleteArtworkFeelingCommand;
import com.example.demo.domain.artworkcommunication.application.command.DeleteArtworkFeelingService;
import com.example.demo.domain.artworkcommunication.application.command.UpdateArtworkFeelingCommand;
import com.example.demo.domain.artworkcommunication.application.command.UpdateArtworkFeelingService;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingResult;
import com.example.demo.domain.artworkcommunication.application.result.UpdatedArtworkFeelingResult;
import com.example.demo.domain.artworkcommunication.presentation.docs.ArtworkFeelingApiDocs;
import com.example.demo.domain.artworkcommunication.presentation.mapper.ArtworkFeelingPresentationMapper;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.UpdateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.UpdatedArtworkFeelingResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/artworks/{artworkId}/feelings")
public class ArtworkFeelingController implements ArtworkFeelingApiDocs {

    private final CreateArtworkFeelingService createArtworkFeelingService;
    private final UpdateArtworkFeelingService updateArtworkFeelingService;
    private final DeleteArtworkFeelingService deleteArtworkFeelingService;
    private final ArtworkFeelingPresentationMapper mapper;

    @Override
    @PostMapping
    // 감상평 작성
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

    @Override
    @PatchMapping("/{feelingId}")
    // 감상평 수정
    public ApiResponseBody<UpdatedArtworkFeelingResponse> updateFeeling(
            @PathVariable Long artworkId,
            @PathVariable Long feelingId,
            @RequestHeader("X-User-Id") Long userId, //테스트용
            @Valid @RequestBody UpdateArtworkFeelingRequest request,
            HttpServletRequest httpServletRequest
    ) {
        UpdateArtworkFeelingCommand command =
                mapper.toCommand(artworkId, feelingId, userId, request);

        UpdatedArtworkFeelingResult result =
                updateArtworkFeelingService.updateFeeling(command);

        UpdatedArtworkFeelingResponse response =
                mapper.toResponse(result);

        return ApiResponseBody.success(response, httpServletRequest);
    }

    @Override
    @DeleteMapping("/{feelingId}")
    // 감상평 삭제
    public ApiResponseBody<Void> deleteFeeling(
            @PathVariable Long artworkId,
            @PathVariable Long feelingId,
            @RequestHeader("X-User-Id") Long userId, //테스트용
            HttpServletRequest httpServletRequest
    ) {
        DeleteArtworkFeelingCommand command =
                new DeleteArtworkFeelingCommand(artworkId, feelingId, userId);

        deleteArtworkFeelingService.deleteFeeling(command);

        return ApiResponseBody.success(null, httpServletRequest);
    }
}
