package com.example.demo.domain.artworkcommunication.presentation;

import com.example.demo.domain.artworkcommunication.application.command.*;
import com.example.demo.domain.artworkcommunication.application.query.GetArtworkFeelingsService;
import com.example.demo.domain.artworkcommunication.application.result.*;
import com.example.demo.domain.artworkcommunication.presentation.docs.ArtworkFeelingApiDocs;
import com.example.demo.domain.artworkcommunication.presentation.mapper.ArtworkFeelingPresentationMapper;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingReplyRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.UpdateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.*;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/artworks/{artworkId}/feelings")
public class ArtworkFeelingController implements ArtworkFeelingApiDocs {

  private final CreateArtworkFeelingService createArtworkFeelingService;
  private final UpdateArtworkFeelingService updateArtworkFeelingService;
  private final DeleteArtworkFeelingService deleteArtworkFeelingService;
  private final CreateArtworkFeelingReplyService createArtworkFeelingReplyService;
  private final GetArtworkFeelingsService getArtworkFeelingsService;
  private final ArtworkFeelingLikeService artworkFeelingLikeService;
  private final ArtworkFeelingPresentationMapper mapper;

  @Override
  @GetMapping
  // 감상평 목록 및 답변 조회
  public ApiResponseBody<ArtworkFeelingListResponse> getFeelings(
      @PathVariable Long artworkId,
      @RequestParam(required = false) @Positive Long cursorId,
      HttpServletRequest httpServletRequest) {
    ArtworkFeelingListResult result =
        getArtworkFeelingsService.getFeelings(mapper.toQuery(artworkId, cursorId));

    ArtworkFeelingListResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping
  // 감상평 작성
  public ApiResponseBody<ArtworkFeelingResponse> createFeeling(
      @PathVariable Long artworkId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      @Valid @RequestBody CreateArtworkFeelingRequest request,
      HttpServletRequest httpServletRequest) {
    ArtworkFeelingCommand command = mapper.toCommand(artworkId, userId, request);

    ArtworkFeelingResult result = createArtworkFeelingService.createFeeling(command);

    ArtworkFeelingResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("{feelingId}/reply")
  // 감상평 답변 등록
  public ApiResponseBody<ArtworkFeelingReplyResponse> createFeelingReply(
      @PathVariable Long artworkId,
      @PathVariable Long feelingId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      @Valid @RequestBody CreateArtworkFeelingReplyRequest request,
      HttpServletRequest httpServletRequest) {
    ArtworkFeelingReplyCommand command = mapper.toCommand(artworkId, feelingId, userId, request);

    ArtworkFeelingReplyResult result = createArtworkFeelingReplyService.createFeelingReply(command);

    ArtworkFeelingReplyResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PatchMapping("/{feelingId}")
  // 감상평 수정
  public ApiResponseBody<UpdatedArtworkFeelingResponse> updateFeeling(
      @PathVariable Long artworkId,
      @PathVariable Long feelingId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      @Valid @RequestBody UpdateArtworkFeelingRequest request,
      HttpServletRequest httpServletRequest) {
    UpdateArtworkFeelingCommand command = mapper.toCommand(artworkId, feelingId, userId, request);

    UpdatedArtworkFeelingResult result = updateArtworkFeelingService.updateFeeling(command);

    UpdatedArtworkFeelingResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @DeleteMapping("/{feelingId}")
  // 감상평 삭제
  public ApiResponseBody<DeletedArtworkFeelingResponse> deleteFeeling(
      @PathVariable Long artworkId,
      @PathVariable Long feelingId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      HttpServletRequest httpServletRequest) {
    DeleteArtworkFeelingCommand command =
        new DeleteArtworkFeelingCommand(artworkId, feelingId, userId);

    DeletedArtworkFeelingResult result = deleteArtworkFeelingService.deleteFeeling(command);

    DeletedArtworkFeelingResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{feelingId}/like")
  // 감상평 좋아요 등록 및 취소
  public ApiResponseBody<ArtworkFeelingLikeResponse> feelingLike(
      @PathVariable Long artworkId,
      @PathVariable Long feelingId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      HttpServletRequest httpServletRequest) {
    ArtworkFeelingLikeCommand command = new ArtworkFeelingLikeCommand(artworkId, feelingId, userId);

    ArtworkFeelingLikeResult result = artworkFeelingLikeService.artworkFeelingLike(command);

    ArtworkFeelingLikeResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }
}
