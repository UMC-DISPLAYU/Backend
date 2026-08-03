package com.example.demo.domain.artworkcommunication.presentation;

import com.example.demo.domain.artworkcommunication.application.query.GetMyArtworkFeelingsService;
import com.example.demo.domain.artworkcommunication.application.result.MyArtworkFeelingListResult;
import com.example.demo.domain.artworkcommunication.presentation.docs.MyArtworkFeelingApiDocs;
import com.example.demo.domain.artworkcommunication.presentation.mapper.MyArtworkFeelingPresentationMapper;
import com.example.demo.domain.artworkcommunication.presentation.response.MyArtworkFeelingListResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/artworks/feelings/me")
public class MyArtworkFeelingController implements MyArtworkFeelingApiDocs {

  private final GetMyArtworkFeelingsService getMyArtworkFeelingsService;
  private final MyArtworkFeelingPresentationMapper mapper;

  @Override
  @GetMapping
  @SecurityRequirement(name = "Authorization")
  public ApiResponseBody<MyArtworkFeelingListResponse> getMyFeelings(
      @AuthenticationPrincipal AuthUser user,
      @RequestParam(required = false) String cursor,
      @RequestParam(defaultValue = "10") int size,
      HttpServletRequest httpServletRequest) {
    MyArtworkFeelingListResult result =
        getMyArtworkFeelingsService.getMyFeelings(
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
