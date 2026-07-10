package com.example.demo.domain.lounge.presentation;

import com.example.demo.domain.lounge.application.command.LoungeCommentCommandService;
import com.example.demo.domain.lounge.application.query.LoungeCommentQueryService;
import com.example.demo.domain.lounge.presentation.mapper.LoungePresentationMapper;
import com.example.demo.domain.lounge.presentation.request.LoungeCommentRequest;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentLikeResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentListResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoungeCommentController {

    private static final Long TEMP_USER_ID = 1L;

    private final LoungeCommentCommandService loungeCommentCommandService;
    private final LoungeCommentQueryService loungeCommentQueryService;
    private final LoungePresentationMapper mapper;

    public LoungeCommentController(
            LoungeCommentCommandService loungeCommentCommandService,
            LoungeCommentQueryService loungeCommentQueryService,
            LoungePresentationMapper mapper) {
        this.loungeCommentCommandService = loungeCommentCommandService;
        this.loungeCommentQueryService = loungeCommentQueryService;
        this.mapper = mapper;
    }

    @PostMapping("/api/v1/lounge/posts/{loungePostId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseBody<LoungeCommentListResponse> createComment(
            @PathVariable Long loungePostId,
            @Valid @RequestBody LoungeCommentRequest loungeCommentRequest,
            HttpServletRequest request) {
        return ApiResponseBody.success(
                mapper.toResponse(
                        loungeCommentCommandService.createComment(
                                loungePostId,
                                TEMP_USER_ID,
                                loungeCommentRequest.toCommand())),
                request);
    }

    @PostMapping("/api/v1/lounge/posts/{loungePostId}/comments/{parentCommentId}/replies")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponseBody<LoungeCommentListResponse> createReply(
            @PathVariable Long loungePostId,
            @PathVariable Long parentCommentId,
            @Valid @RequestBody LoungeCommentRequest loungeCommentRequest,
            HttpServletRequest request) {
        return ApiResponseBody.success(
                mapper.toResponse(
                        loungeCommentCommandService.createReply(
                                loungePostId,
                                parentCommentId,
                                TEMP_USER_ID,
                                loungeCommentRequest.toCommand())),
                request);
    }

    @GetMapping("/api/v1/lounge/posts/{loungePostId}/comments")
    public ApiResponseBody<List<LoungeCommentListResponse>> getComments(
            @PathVariable Long loungePostId,
            HttpServletRequest request) {
        return ApiResponseBody.success(
                mapper.toCommentResponses(loungeCommentQueryService.getComments(loungePostId)),
                request);
    }

    @PatchMapping("/api/v1/lounge/comments/{loungeCommentId}")
    public ApiResponseBody<Void> updateComment(
            @PathVariable Long loungeCommentId,
            @Valid @RequestBody LoungeCommentRequest loungeCommentRequest,
            HttpServletRequest request) {
        loungeCommentCommandService.updateComment(
                loungeCommentId,
                TEMP_USER_ID,
                loungeCommentRequest.toCommand());

        return ApiResponseBody.success(null, request);
    }

    @DeleteMapping("/api/v1/lounge/comments/{loungeCommentId}")
    public ApiResponseBody<Void> deleteComment(
            @PathVariable Long loungeCommentId,
            HttpServletRequest request) {
        loungeCommentCommandService.deleteComment(loungeCommentId, TEMP_USER_ID);
        return ApiResponseBody.success(null, request);
    }

    @PostMapping("/api/v1/lounge/comments/{loungeCommentId}/likes")
    public ApiResponseBody<LoungeCommentLikeResponse> likeComment(
            @PathVariable Long loungeCommentId,
            HttpServletRequest request) {
        return ApiResponseBody.success(
                mapper.toResponse(loungeCommentCommandService.likeComment(loungeCommentId, TEMP_USER_ID)),
                request);
    }

    @DeleteMapping("/api/v1/lounge/comments/{loungeCommentId}/likes")
    public ApiResponseBody<LoungeCommentLikeResponse> cancelLikeComment(
            @PathVariable Long loungeCommentId,
            HttpServletRequest request) {
        return ApiResponseBody.success(
                mapper.toResponse(loungeCommentCommandService.cancelLikeComment(loungeCommentId, TEMP_USER_ID)),
                request);
    }
}
