package com.example.demo.domain.lounge.presentation.mapper;

import com.example.demo.domain.lounge.application.result.LoungeCommentLikeResult;
import com.example.demo.domain.lounge.application.result.LoungeCommentListResult;
import com.example.demo.domain.lounge.application.result.LoungePostDetailResult;
import com.example.demo.domain.lounge.application.result.LoungePostLikeResult;
import com.example.demo.domain.lounge.application.result.LoungePostListResult;
import com.example.demo.domain.lounge.application.result.LoungePostScrapResult;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentLikeResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentListResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostDetailResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostLikeResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostListResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostScrapResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LoungePresentationMapper {

  public LoungePostListResponse toResponse(LoungePostListResult result) {
    return new LoungePostListResponse(
        result.loungePostId(),
        result.authorUserId(),
        result.title(),
        result.postImageUrl(),
        result.category(),
        result.likeCount(),
        result.commentCount(),
        result.scrapCount(),
        result.createdAt());
  }

  public LoungePostDetailResponse toResponse(LoungePostDetailResult result) {
    return new LoungePostDetailResponse(
        result.loungePostId(),
        result.authorUserId(),
        result.title(),
        result.postImageUrl(),
        result.content(),
        result.category(),
        result.status(),
        result.likeCount(),
        result.commentCount(),
        result.scrapCount(),
        result.createdAt(),
        result.updatedAt());
  }

  public LoungePostLikeResponse toResponse(LoungePostLikeResult result) {
    return new LoungePostLikeResponse(result.loungePostId(), result.liked(), result.likeCount());
  }

  public LoungePostScrapResponse toResponse(LoungePostScrapResult result) {
    return new LoungePostScrapResponse(
        result.loungePostId(), result.scrapped(), result.scrapCount());
  }

  public LoungeCommentListResponse toResponse(LoungeCommentListResult result) {
    return new LoungeCommentListResponse(
        result.loungeCommentId(),
        result.parentCommentId(),
        result.authorUserId(),
        result.content(),
        result.likeCount(),
        result.createdAt(),
        result.updatedAt(),
        result.replies().stream().map(this::toResponse).toList());
  }

  public List<LoungeCommentListResponse> toCommentResponses(List<LoungeCommentListResult> results) {
    return results.stream().map(this::toResponse).toList();
  }

  public LoungeCommentLikeResponse toResponse(LoungeCommentLikeResult result) {
    return new LoungeCommentLikeResponse(
        result.loungeCommentId(), result.liked(), result.likeCount());
  }
}
