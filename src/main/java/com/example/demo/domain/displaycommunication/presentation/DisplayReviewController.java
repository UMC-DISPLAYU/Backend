package com.example.demo.domain.displaycommunication.presentation;

import com.example.demo.domain.displaycommunication.application.command.CreateDisplayReviewCommand;
import com.example.demo.domain.displaycommunication.application.command.CreateDisplayReviewService;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewResult;
import com.example.demo.domain.displaycommunication.presentation.docs.DisplayReviewApiDocs;
import com.example.demo.domain.displaycommunication.presentation.mapper.DisplayReviewPresentationMapper;
import com.example.demo.domain.displaycommunication.presentation.request.CreateDisplayReviewRequest;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DisplayReviewController implements DisplayReviewApiDocs {
  private final CreateDisplayReviewService createDisplayReviewService;
  private final DisplayReviewPresentationMapper mapper;

  @Override
  @PostMapping("/api/v1/display/{displayId}/reviews")
  public ApiResponseBody<DisplayReviewResponse> createReview(
      @PathVariable Long displayId,
      @AuthenticationPrincipal AuthUser user,
      @Valid @RequestBody CreateDisplayReviewRequest request,
      HttpServletRequest httpServletRequest) {
    CreateDisplayReviewCommand command = mapper.toCommand(displayId, requireUserId(user), request);
    DisplayReviewResult result = createDisplayReviewService.create(command);
    DisplayReviewResponse response = mapper.toResponse(result);
    return ApiResponseBody.success(response, httpServletRequest);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
