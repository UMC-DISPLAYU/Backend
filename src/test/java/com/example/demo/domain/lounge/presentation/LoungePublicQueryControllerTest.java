package com.example.demo.domain.lounge.presentation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.demo.domain.lounge.application.command.LoungeCommentCommandService;
import com.example.demo.domain.lounge.application.command.LoungePostCommandService;
import com.example.demo.domain.lounge.application.query.LoungeCommentQueryService;
import com.example.demo.domain.lounge.application.query.LoungePostQueryService;
import com.example.demo.domain.lounge.presentation.mapper.LoungePresentationMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

class LoungePublicQueryControllerTest {

  @Test
  void anonymousRequestsPassNullViewerUserId() {
    LoungePostQueryService postQueryService = mock(LoungePostQueryService.class);
    LoungeCommentQueryService commentQueryService = mock(LoungeCommentQueryService.class);
    LoungePresentationMapper mapper = mock(LoungePresentationMapper.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    LoungePostController postController =
        new LoungePostController(mock(LoungePostCommandService.class), postQueryService, mapper);
    LoungeCommentController commentController =
        new LoungeCommentController(
            mock(LoungeCommentCommandService.class), commentQueryService, mapper);

    postController.getPosts(null, null, 10, null, request);
    postController.getPostDetail(1L, null, request);
    commentController.getComments(1L, null, 10, null, request);
    commentController.getReplies(1L, null, 10, null, request);

    verify(postQueryService).getPosts(null, null, 10, null);
    verify(postQueryService).getPostDetail(1L, null);
    verify(commentQueryService).getComments(1L, null, 10, null);
    verify(commentQueryService).getReplies(1L, null, 10, null);
  }
}
