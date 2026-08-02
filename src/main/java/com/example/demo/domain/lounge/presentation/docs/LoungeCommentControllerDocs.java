package com.example.demo.domain.lounge.presentation.docs;

import com.example.demo.domain.lounge.presentation.request.LoungeCommentRequest;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentCursorResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentLikeResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentListResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostCursorResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeReplyCursorResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Lounge Comment", description = "라운지 댓글 API")
@SecurityRequirement(name = "Authorization")
public interface LoungeCommentControllerDocs {

  @Operation(summary = "라운지 댓글 생성", description = "라운지 게시글에 댓글을 생성합니다.")
  ApiResponseBody<LoungeCommentListResponse> createComment(
      @PathVariable Long loungePostId,
      @Valid @RequestBody LoungeCommentRequest loungeCommentRequest,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "라운지 답글 생성", description = "라운지 댓글에 답글을 생성합니다.")
  ApiResponseBody<LoungeCommentListResponse> createReply(
      @PathVariable Long parentCommentId,
      @Valid @RequestBody LoungeCommentRequest loungeCommentRequest,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "라운지 댓글 삭제", description = "작성자가 라운지 댓글 또는 답글을 삭제합니다.")
  ApiResponseBody<Void> deleteComment(
      @PathVariable Long loungeCommentId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "라운지 댓글 좋아요", description = "라운지 댓글 또는 답글에 좋아요를 추가합니다.")
  ApiResponseBody<LoungeCommentLikeResponse> likeComment(
      @PathVariable Long loungeCommentId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "라운지 댓글 좋아요 취소", description = "라운지 댓글 또는 답글 좋아요를 취소합니다.")
  ApiResponseBody<LoungeCommentLikeResponse> cancelLikeComment(
      @PathVariable Long loungeCommentId, AuthUser user, HttpServletRequest request);

  @Operation(
      summary = "라운지 댓글 목록 조회",
      description =
          "게시글의 댓글 목록을 커서 방식으로 조회합니다. 활성 답글이 남아 있는 삭제된 부모 댓글은 DELETED 상태로 포함하며 본문과 이미지는 반환하지 않습니다.")
  ApiResponseBody<LoungeCommentCursorResponse> getComments(
      @PathVariable Long loungePostId,
      @Parameter(description = "마지막으로 조회한 댓글 ID. 첫 요청이면 전달하지 않음") @RequestParam(required = false)
          Long cursorId,
      @Parameter(description = "한 번에 불러올 댓글 개수") @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
      AuthUser user,
      HttpServletRequest request);

  @Operation(
      summary = "라운지 답글 목록 조회",
      description = "댓글의 활성 답글 목록을 커서 방식으로 조회합니다. 부모 댓글이 삭제되어도 남아 있는 답글을 조회할 수 있습니다.")
  ApiResponseBody<LoungeReplyCursorResponse> getReplies(
      @PathVariable Long parentCommentId,
      @Parameter(description = "마지막으로 조회한 답글 ID. 첫 요청이면 전달하지 않음") @RequestParam(required = false)
          Long cursorId,
      @Parameter(description = "한 번에 불러올 답글 개수") @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
      AuthUser user,
      HttpServletRequest request);

  @Operation(
      summary = "내가 댓글을 작성한 라운지 게시글 조회",
      description = "로그인 사용자가 댓글 또는 답글을 작성한 게시글을 중복 없이 조회합니다.")
  ApiResponseBody<LoungePostCursorResponse> getMyComments(
      @Parameter(description = "마지막 게시글의 최근 댓글 ID. 첫 요청이면 전달하지 않음") @RequestParam(required = false)
          Long cursorId,
      @Parameter(description = "한 번에 불러올 게시글 개수")
          @RequestParam(defaultValue = "10")
          @Min(1) @Max(50) int size,
      AuthUser user,
      HttpServletRequest request);
}
