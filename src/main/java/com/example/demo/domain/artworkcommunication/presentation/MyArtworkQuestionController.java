package com.example.demo.domain.artworkcommunication.presentation;

import com.example.demo.domain.artworkcommunication.application.query.GetMyArtworkQuestionsService;
import com.example.demo.domain.artworkcommunication.application.result.MyArtworkQuestionListResult;
import com.example.demo.domain.artworkcommunication.presentation.docs.MyArtworkQuestionApiDocs;
import com.example.demo.domain.artworkcommunication.presentation.mapper.MyArtworkQuestionPresentationMapper;
import com.example.demo.domain.artworkcommunication.presentation.response.MyArtworkQuestionListResponse;
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
@RequestMapping("/api/v1/artworks/questions/me")
public class MyArtworkQuestionController implements MyArtworkQuestionApiDocs {

  private final GetMyArtworkQuestionsService getMyArtworkQuestionsService;
  private final MyArtworkQuestionPresentationMapper mapper;

  @Override
  @GetMapping
  @SecurityRequirement(name = "Authorization")
  public ApiResponseBody<MyArtworkQuestionListResponse> getMyQuestions(
      @AuthenticationPrincipal AuthUser user,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "10") int size,
      HttpServletRequest httpServletRequest) {
    MyArtworkQuestionListResult result =
        getMyArtworkQuestionsService.getMyQuestions(
            mapper.toQuery(requireUserId(user), cursor, size));
    return ApiResponseBody.success(mapper.toResponse(result), httpServletRequest);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
