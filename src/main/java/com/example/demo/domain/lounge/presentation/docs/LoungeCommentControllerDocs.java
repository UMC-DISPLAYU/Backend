package com.example.demo.domain.lounge.presentation.docs;

import com.example.demo.domain.lounge.presentation.request.LoungeCommentRequest;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentLikeResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentListResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Lounge Comment", description = "라운지 댓글 API")
public interface LoungeCommentControllerDocs {

  @Operation(summary = "라운지 댓글 생성", description = "라운지 게시글에 댓글을 생성합니다.")
  ApiResponseBody<LoungeCommentListResponse> createComment(
      @PathVariable Long loungePostId,
      @Valid @RequestBody LoungeCommentRequest loungeCommentRequest,
      HttpServletRequest request);

  @Operation(summary = "라운지 답글 생성", description = "라운지 댓글에 답글을 생성합니다.")
  ApiResponseBody<LoungeCommentListResponse> createReply(
      @PathVariable Long loungePostId,
      @PathVariable Long parentCommentId,
      @Valid @RequestBody LoungeCommentRequest loungeCommentRequest,
      HttpServletRequest request);

  @Operation(summary = "라운지 댓글 수정", description = "작성자가 라운지 댓글 또는 답글을 수정합니다.")
  ApiResponseBody<Void> updateComment(
      @PathVariable Long loungeCommentId,
      @Valid @RequestBody LoungeCommentRequest loungeCommentRequest,
      HttpServletRequest request);

  @Operation(summary = "라운지 댓글 삭제", description = "작성자가 라운지 댓글 또는 답글을 삭제합니다.")
  ApiResponseBody<Void> deleteComment(
      @PathVariable Long loungeCommentId, HttpServletRequest request);

  @Operation(summary = "라운지 댓글 좋아요", description = "라운지 댓글 또는 답글에 좋아요를 추가합니다.")
  ApiResponseBody<LoungeCommentLikeResponse> likeComment(
      @PathVariable Long loungeCommentId, HttpServletRequest request);

  @Operation(summary = "라운지 댓글 좋아요 취소", description = "라운지 댓글 또는 답글 좋아요를 취소합니다.")
  ApiResponseBody<LoungeCommentLikeResponse> cancelLikeComment(
      @PathVariable Long loungeCommentId, HttpServletRequest request);

  @Operation(summary = "라운지 댓글 목록 조회", description = "게시글의 댓글과 답글 목록을 조회합니다.")
  ApiResponseBody<List<LoungeCommentListResponse>> getComments(
      @PathVariable Long loungePostId, HttpServletRequest request);
}
