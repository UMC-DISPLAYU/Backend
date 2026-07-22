package com.example.demo.domain.lounge.presentation.mapper;

import com.example.demo.domain.lounge.application.result.LoungeCommentCursorResult;
import com.example.demo.domain.lounge.application.result.LoungeCommentLikeResult;
import com.example.demo.domain.lounge.application.result.LoungeCommentListResult;
import com.example.demo.domain.lounge.application.result.LoungePostCursorResult;
import com.example.demo.domain.lounge.application.result.LoungePostDetailResult;
import com.example.demo.domain.lounge.application.result.LoungePostLikeResult;
import com.example.demo.domain.lounge.application.result.LoungePostListResult;
import com.example.demo.domain.lounge.application.result.LoungePostScrapResult;
import com.example.demo.domain.lounge.application.result.LoungeReplyCursorResult;
import com.example.demo.domain.lounge.application.result.WriterView;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentCursorResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentLikeResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentListResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostCursorResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostDetailResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostLikeResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostListResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostScrapResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeReplyCursorResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeReplyListResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeWriterResponse;
import org.springframework.stereotype.Component;

@Component
public class LoungePresentationMapper {

  public LoungePostListResponse toResponse(LoungePostListResult result) {
    return new LoungePostListResponse(
        result.loungePostId(),
        result.category(),
        result.title(),
        result.postImageUrl(),
        toResponse(result.writer()),
        result.createdAt(),
        result.commentCount(),
        result.likeCount(),
        result.isLiked(),
        result.isMyPost());
  }

  public LoungePostCursorResponse toResponse(LoungePostCursorResult result) {
    return new LoungePostCursorResponse(
        result.posts().stream().map(this::toResponse).toList(),
        result.nextCursorId(),
        result.size(),
        result.hasNext());
  }

  public LoungePostDetailResponse toResponse(LoungePostDetailResult result) {
    return new LoungePostDetailResponse(
        result.loungePostId(),
        result.title(),
        result.postImageUrl(),
        result.content(),
        result.category(),
        result.postStatus(),
        toResponse(result.writer()),
        result.createdAt(),
        result.updatedAt(),
        result.commentCount(),
        result.likeCount(),
        result.isLiked(),
        result.isScrapped(),
        result.isMyPost());
  }

  public LoungeWriterResponse toResponse(WriterView writer) {
    return new LoungeWriterResponse(writer.userId(), writer.nickname(), writer.profileImageUrl());
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
        result.content(),
        result.commentStatus(),
        toResponse(result.writer()),
        result.createdAt(),
        result.updatedAt(),
        result.likeCount(),
        result.replyCount(),
        result.isLiked(),
        result.isMyComment());
  }

  public LoungeCommentCursorResponse toResponse(LoungeCommentCursorResult result) {
    return new LoungeCommentCursorResponse(
        result.comments().stream().map(this::toResponse).toList(),
        result.nextCursorId(),
        result.size(),
        result.hasNext());
  }

  public LoungeReplyCursorResponse toResponse(LoungeReplyCursorResult result) {
    return new LoungeReplyCursorResponse(
        result.replies().stream().map(this::toReplyResponse).toList(),
        result.nextCursorId(),
        result.size(),
        result.hasNext());
  }

  private LoungeReplyListResponse toReplyResponse(LoungeCommentListResult result) {
    return new LoungeReplyListResponse(
        result.loungeCommentId(),
        result.parentCommentId(),
        result.content(),
        result.commentStatus(),
        toResponse(result.writer()),
        result.createdAt(),
        result.updatedAt(),
        result.likeCount(),
        result.isLiked(),
        result.isMyComment());
  }

  public LoungeCommentLikeResponse toResponse(LoungeCommentLikeResult result) {
    return new LoungeCommentLikeResponse(
        result.loungeCommentId(), result.liked(), result.likeCount());
  }
}
