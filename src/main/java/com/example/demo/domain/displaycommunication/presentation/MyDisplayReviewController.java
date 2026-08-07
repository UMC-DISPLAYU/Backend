package com.example.demo.domain.displaycommunication.presentation;

import com.example.demo.domain.displaycommunication.application.query.GetMyDisplayReviewsService;
import com.example.demo.domain.displaycommunication.application.result.MyDisplayReviewListResult;
import com.example.demo.domain.displaycommunication.presentation.docs.MyDisplayReviewApiDocs;
import com.example.demo.domain.displaycommunication.presentation.mapper.MyDisplayReviewPresentationMapper;
import com.example.demo.domain.displaycommunication.presentation.response.MyDisplayReviewListResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/displays/reviews/me")
public class MyDisplayReviewController implements MyDisplayReviewApiDocs {

  private final GetMyDisplayReviewsService getMyDisplayReviewsService;
  private final MyDisplayReviewPresentationMapper mapper;

  @Override
  @GetMapping
  @SecurityRequirement(name = "Authorization")
  // 내가 작성한 후기 목록 조회
  public ApiResponseBody<MyDisplayReviewListResponse> getMyReviews(
      @AuthenticationPrincipal AuthUser user,
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      HttpServletRequest httpServletRequest) {
    MyDisplayReviewListResult result =
        getMyDisplayReviewsService.getMyReviews(
            mapper.toQuery(requireUserId(user), cursorId, size));
    return ApiResponseBody.success(mapper.toResponse(result), httpServletRequest);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
