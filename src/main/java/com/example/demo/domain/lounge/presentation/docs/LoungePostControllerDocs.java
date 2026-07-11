package com.example.demo.domain.lounge.presentation.docs;

import com.example.demo.domain.lounge.presentation.request.LoungePostRequest;
import com.example.demo.domain.lounge.presentation.response.LoungePostDetailResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostLikeResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostListResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostScrapResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Lounge Post", description = "라운지 게시글 API")
public interface LoungePostControllerDocs {

    @Operation(summary = "라운지 게시글 생성", description = "라운지 게시글을 생성합니다.")
    ApiResponseBody<LoungePostDetailResponse> createPost(
            @Valid @RequestBody LoungePostRequest loungePostRequest,
            HttpServletRequest request);

    @Operation(summary = "라운지 게시글 수정", description = "작성자가 라운지 게시글을 수정합니다.")
    ApiResponseBody<LoungePostDetailResponse> updatePost(
            @PathVariable Long loungePostId,
            @Valid @RequestBody LoungePostRequest loungePostRequest,
            HttpServletRequest request);

    @Operation(summary = "라운지 게시글 삭제", description = "작성자가 라운지 게시글을 삭제합니다.")
    ApiResponseBody<Void> deletePost(
            @PathVariable Long loungePostId,
            HttpServletRequest request);

    @Operation(summary = "라운지 게시글 좋아요", description = "라운지 게시글에 좋아요를 추가합니다.")
    ApiResponseBody<LoungePostLikeResponse> likePost(
            @PathVariable Long loungePostId,
            HttpServletRequest request);

    @Operation(summary = "라운지 게시글 좋아요 취소", description = "라운지 게시글 좋아요를 취소합니다.")
    ApiResponseBody<LoungePostLikeResponse> cancelLikePost(
            @PathVariable Long loungePostId,
            HttpServletRequest request);

    @Operation(summary = "라운지 게시글 스크랩", description = "라운지 게시글을 스크랩합니다.")
    ApiResponseBody<LoungePostScrapResponse> scrapPost(
            @PathVariable Long loungePostId,
            HttpServletRequest request);

    @Operation(summary = "라운지 게시글 스크랩 취소", description = "라운지 게시글 스크랩을 취소합니다.")
    ApiResponseBody<LoungePostScrapResponse> cancelScrapPost(
            @PathVariable Long loungePostId,
            HttpServletRequest request);

    @Operation(summary = "라운지 게시글 목록 조회", description = "라운지 게시글 목록을 조회합니다.")
    ApiResponseBody<List<LoungePostListResponse>> getPosts(HttpServletRequest request);

    @Operation(summary = "라운지 게시글 상세 조회", description = "라운지 게시글 상세 정보를 조회합니다.")
    ApiResponseBody<LoungePostDetailResponse> getPostDetail(
            @PathVariable Long loungePostId,
            HttpServletRequest request);
}
