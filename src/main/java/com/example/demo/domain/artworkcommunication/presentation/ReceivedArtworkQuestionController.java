package com.example.demo.domain.artworkcommunication.presentation;

import com.example.demo.domain.artworkcommunication.application.query.GetReceivedArtworkQuestionsService;
import com.example.demo.domain.artworkcommunication.application.result.ReceivedArtworkQuestionListResult;
import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import com.example.demo.domain.artworkcommunication.presentation.docs.ReceivedArtworkQuestionApiDocs;
import com.example.demo.domain.artworkcommunication.presentation.mapper.ReceivedArtworkQuestionPresentationMapper;
import com.example.demo.domain.artworkcommunication.presentation.response.ReceivedArtworkQuestionListResponse;
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
@RequestMapping("/api/v1/artworks/questions/received")
public class ReceivedArtworkQuestionController implements ReceivedArtworkQuestionApiDocs {

  private final GetReceivedArtworkQuestionsService getReceivedArtworkQuestionsService;
  private final ReceivedArtworkQuestionPresentationMapper mapper;

  @Override
  @GetMapping
  @SecurityRequirement(name = "Authorization")
  public ApiResponseBody<ReceivedArtworkQuestionListResponse> getReceivedQuestions(
      @AuthenticationPrincipal AuthUser user,
      @RequestParam AnswerStatus answerStatus,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "10") int size,
      HttpServletRequest httpServletRequest) {
    ReceivedArtworkQuestionListResult result =
        getReceivedArtworkQuestionsService.getReceivedQuestions(
            mapper.toQuery(requireUserId(user), answerStatus, cursor, size));
    return ApiResponseBody.success(mapper.toResponse(result), httpServletRequest);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
