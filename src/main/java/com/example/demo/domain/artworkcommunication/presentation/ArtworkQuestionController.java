package com.example.demo.domain.artworkcommunication.presentation;

import com.example.demo.domain.artworkcommunication.application.command.*;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionReplyResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionResult;
import com.example.demo.domain.artworkcommunication.application.result.DeletedArtworkQuestionResult;
import com.example.demo.domain.artworkcommunication.presentation.docs.ArtworkQuestionApiDocs;
import com.example.demo.domain.artworkcommunication.presentation.mapper.ArtworkQuestionPresentationMapper;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkQuestionReplyRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkQuestionRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.UpdateArtworkQuestionRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.*;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/artworks/{artworkId}/questions")
public class ArtworkQuestionController implements ArtworkQuestionApiDocs {

  private final CreateArtworkQuestionService createArtworkQuestionService;
  private final CreateArtworkQuestionReplyService createArtworkQuestionReplyService;
  private final UpdateArtworkQuestionService updateArtworkQuestionService;
  private final DeleteArtworkQuestionService deleteArtworkQuestionService;
  private final ArtworkQuestionPresentationMapper mapper;

  @Override
  @PostMapping
  // 질문 등록
  public ApiResponseBody<ArtworkQuestionResponse> createQuestion(
      @PathVariable Long artworkId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      @Valid @RequestBody CreateArtworkQuestionRequest request,
      HttpServletRequest httpServletRequest) {
    CreateArtworkQuestionCommand command = mapper.toCommand(artworkId, userId, request);

    ArtworkQuestionResult result = createArtworkQuestionService.createQuestion(command);

    ArtworkQuestionResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{questionId}/reply")
  // 질문 답변 등록
  public ApiResponseBody<ArtworkQuestionReplyResponse> createQuestionReply(
      @PathVariable Long artworkId,
      @PathVariable Long questionId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      @Valid @RequestBody CreateArtworkQuestionReplyRequest request,
      HttpServletRequest httpServletRequest) {
    ArtworkQuestionReplyCommand command = mapper.toCommand(artworkId, questionId, userId, request);

    ArtworkQuestionReplyResult result =
        createArtworkQuestionReplyService.createQuestionReply(command);

    ArtworkQuestionReplyResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PatchMapping("/{questionId}")
  // 질문 수정
  public ApiResponseBody<ArtworkQuestionResponse> updateQuestion(
      @PathVariable Long artworkId,
      @PathVariable Long questionId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      @Valid @RequestBody UpdateArtworkQuestionRequest request,
      HttpServletRequest httpServletRequest) {
    UpdateArtworkQuestionCommand command = mapper.toCommand(artworkId, questionId, userId, request);

    ArtworkQuestionResult result = updateArtworkQuestionService.updateQuestion(command);

    ArtworkQuestionResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @DeleteMapping("/{questionId}")
  // 질문 삭제
  public ApiResponseBody<DeletedArtworkQuestionResponse> deleteQuestion(
      @PathVariable Long artworkId,
      @PathVariable Long questionId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      HttpServletRequest httpServletRequest) {
    DeleteArtworkQuestionCommand command =
        new DeleteArtworkQuestionCommand(artworkId, questionId, userId);

    DeletedArtworkQuestionResult result = deleteArtworkQuestionService.deleteQuestion(command);

    DeletedArtworkQuestionResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }
}
