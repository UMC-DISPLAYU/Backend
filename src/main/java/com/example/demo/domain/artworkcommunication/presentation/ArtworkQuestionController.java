package com.example.demo.domain.artworkcommunication.presentation;

import com.example.demo.domain.artworkcommunication.application.command.*;
import com.example.demo.domain.artworkcommunication.application.query.GetArtworkQuestionsService;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionLikeResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionListResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionReplyLikeResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionReplyResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionResult;
import com.example.demo.domain.artworkcommunication.application.result.DeletedArtworkQuestionReplyResult;
import com.example.demo.domain.artworkcommunication.application.result.DeletedArtworkQuestionResult;
import com.example.demo.domain.artworkcommunication.presentation.docs.ArtworkQuestionApiDocs;
import com.example.demo.domain.artworkcommunication.presentation.mapper.ArtworkQuestionPresentationMapper;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkQuestionReplyRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkQuestionRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.UpdateArtworkQuestionRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.*;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/artworks/{artworkId}/questions")
public class ArtworkQuestionController implements ArtworkQuestionApiDocs {

  private final CreateArtworkQuestionService createArtworkQuestionService;
  private final CreateArtworkQuestionReplyService createArtworkQuestionReplyService;
  private final GetArtworkQuestionsService getArtworkQuestionsService;
  private final UpdateArtworkQuestionService updateArtworkQuestionService;
  private final DeleteArtworkQuestionService deleteArtworkQuestionService;
  private final DeleteArtworkQuestionReplyService deleteArtworkQuestionReplyService;
  private final ArtworkQuestionLikeService artworkQuestionLikeService;
  private final ArtworkQuestionReplyLikeService artworkQuestionReplyLikeService;
  private final ArtworkQuestionPresentationMapper mapper;

  @Override
  @GetMapping
  // 질문 목록 및 답변 조회
  public ApiResponseBody<ArtworkQuestionListResponse> getQuestions(
      @PathVariable Long artworkId,
      @RequestParam(required = false) @Positive Long cursorId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    ArtworkQuestionListResult result =
        getArtworkQuestionsService.getQuestions(
            mapper.toQuery(artworkId, cursorId, user == null ? null : user.userId()));

    ArtworkQuestionListResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping
  @SecurityRequirement(name = "Authorization")
  // 질문 등록
  public ApiResponseBody<ArtworkQuestionResponse> createQuestion(
      @PathVariable Long artworkId,
      @AuthenticationPrincipal AuthUser user,
      @Valid @RequestBody CreateArtworkQuestionRequest request,
      HttpServletRequest httpServletRequest) {
    CreateArtworkQuestionCommand command =
        mapper.toCommand(artworkId, requireUserId(user), request);

    ArtworkQuestionResult result = createArtworkQuestionService.createQuestion(command);

    ArtworkQuestionResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{questionId}/reply")
  @SecurityRequirement(name = "Authorization")
  // 질문 답변 등록
  public ApiResponseBody<ArtworkQuestionReplyResponse> createQuestionReply(
      @PathVariable Long artworkId,
      @PathVariable Long questionId,
      @AuthenticationPrincipal AuthUser user,
      @Valid @RequestBody CreateArtworkQuestionReplyRequest request,
      HttpServletRequest httpServletRequest) {
    ArtworkQuestionReplyCommand command =
        mapper.toCommand(artworkId, questionId, requireUserId(user), request);

    ArtworkQuestionReplyResult result =
        createArtworkQuestionReplyService.createQuestionReply(command);

    ArtworkQuestionReplyResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PatchMapping("/{questionId}")
  @SecurityRequirement(name = "Authorization")
  // 질문 수정
  public ApiResponseBody<ArtworkQuestionResponse> updateQuestion(
      @PathVariable Long artworkId,
      @PathVariable Long questionId,
      @AuthenticationPrincipal AuthUser user,
      @Valid @RequestBody UpdateArtworkQuestionRequest request,
      HttpServletRequest httpServletRequest) {
    UpdateArtworkQuestionCommand command =
        mapper.toCommand(artworkId, questionId, requireUserId(user), request);

    ArtworkQuestionResult result = updateArtworkQuestionService.updateQuestion(command);

    ArtworkQuestionResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @DeleteMapping("/{questionId}")
  @SecurityRequirement(name = "Authorization")
  // 질문 삭제
  public ApiResponseBody<DeletedArtworkQuestionResponse> deleteQuestion(
      @PathVariable Long artworkId,
      @PathVariable Long questionId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    DeleteArtworkQuestionCommand command =
        new DeleteArtworkQuestionCommand(artworkId, questionId, requireUserId(user));

    DeletedArtworkQuestionResult result = deleteArtworkQuestionService.deleteQuestion(command);

    DeletedArtworkQuestionResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @DeleteMapping("/{questionId}/reply/{questionReplyId}")
  @SecurityRequirement(name = "Authorization")
  // 질문 답변 삭제
  public ApiResponseBody<DeletedArtworkQuestionReplyResponse> deleteQuestionReply(
      @PathVariable Long artworkId,
      @PathVariable Long questionId,
      @PathVariable Long questionReplyId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    DeleteArtworkQuestionReplyCommand command =
        new DeleteArtworkQuestionReplyCommand(
            artworkId, questionId, questionReplyId, requireUserId(user));

    DeletedArtworkQuestionReplyResult result =
        deleteArtworkQuestionReplyService.deleteReply(command);

    return ApiResponseBody.success(mapper.toResponse(result), httpServletRequest);
  }

  @Override
  @PostMapping("/{questionId}/like")
  @SecurityRequirement(name = "Authorization")
  // 질문 좋아요 등록 및 취소
  public ApiResponseBody<ArtworkQuestionLikeResponse> questionLike(
      @PathVariable Long artworkId,
      @PathVariable Long questionId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    ArtworkQuestionLikeCommand command =
        new ArtworkQuestionLikeCommand(artworkId, questionId, requireUserId(user));

    ArtworkQuestionLikeResult result = artworkQuestionLikeService.toggleQuestionLike(command);

    return ApiResponseBody.success(mapper.toResponse(result), httpServletRequest);
  }

  @Override
  @PostMapping("/{questionId}/reply/{questionReplyId}/like")
  @SecurityRequirement(name = "Authorization")
  // 질문 답변 좋아요 등록 및 취소
  public ApiResponseBody<ArtworkQuestionReplyLikeResponse> questionReplyLike(
      @PathVariable Long artworkId,
      @PathVariable Long questionId,
      @PathVariable Long questionReplyId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    ArtworkQuestionReplyLikeCommand command =
        new ArtworkQuestionReplyLikeCommand(
            artworkId, questionId, questionReplyId, requireUserId(user));

    ArtworkQuestionReplyLikeResult result =
        artworkQuestionReplyLikeService.toggleQuestionReplyLike(command);

    return ApiResponseBody.success(mapper.toResponse(result), httpServletRequest);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
