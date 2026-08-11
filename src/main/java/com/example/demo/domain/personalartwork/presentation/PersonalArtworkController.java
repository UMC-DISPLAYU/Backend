package com.example.demo.domain.personalartwork.presentation;

import com.example.demo.domain.personalartwork.application.command.PersonalArtworkCommandService;
import com.example.demo.domain.personalartwork.application.command.PersonalArtworkLikeCommand;
import com.example.demo.domain.personalartwork.application.command.PersonalArtworkLikeCommandService;
import com.example.demo.domain.personalartwork.application.query.PersonalArtworkQueryService;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkLikeResult;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkResult;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkSummaryResult;
import com.example.demo.domain.personalartwork.presentation.docs.PersonalArtworkControllerDocs;
import com.example.demo.domain.personalartwork.presentation.mapper.PersonalArtworkPresentationMapper;
import com.example.demo.domain.personalartwork.presentation.request.PersonalArtworkRequest;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkLikeResponse;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkResponse;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkSummaryResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonalArtworkController implements PersonalArtworkControllerDocs {

  private final PersonalArtworkCommandService personalArtworkCommandService;
  private final PersonalArtworkQueryService personalArtworkQueryService;
  private final PersonalArtworkLikeCommandService personalArtworkLikeCommandService;
  private final PersonalArtworkPresentationMapper mapper;

  public PersonalArtworkController(
      PersonalArtworkCommandService personalArtworkCommandService,
      PersonalArtworkQueryService personalArtworkQueryService,
      PersonalArtworkLikeCommandService personalArtworkLikeCommandService,
      PersonalArtworkPresentationMapper mapper) {
    this.personalArtworkCommandService = personalArtworkCommandService;
    this.personalArtworkQueryService = personalArtworkQueryService;
    this.personalArtworkLikeCommandService = personalArtworkLikeCommandService;
    this.mapper = mapper;
  }

  @PostMapping("/api/v1/personal-artworks")
  @ResponseStatus(HttpStatus.CREATED)
  @Override
  public ApiResponseBody<PersonalArtworkResponse> createPersonalArtwork(
      @RequestBody PersonalArtworkRequest personalArtworkRequest,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    Long personalArtworkId =
        personalArtworkCommandService.createPersonalArtwork(
            requireUserId(user), mapper.toCommand(personalArtworkRequest));
    PersonalArtworkResult result =
        personalArtworkQueryService.getPersonalArtworkDetail(
            personalArtworkId, requireUserId(user));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/personal-artworks")
  @Override
  public ApiResponseBody<List<PersonalArtworkSummaryResponse>> getPersonalArtworks(
      @RequestParam Long userId, HttpServletRequest request) {
    List<PersonalArtworkSummaryResult> results =
        personalArtworkQueryService.getPersonalArtworksByUser(userId);
    return ApiResponseBody.success(results.stream().map(mapper::toResponse).toList(), request);
  }

  @GetMapping("/api/v1/personal-artworks/{personalArtworkId}")
  @Override
  public ApiResponseBody<PersonalArtworkResponse> getPersonalArtworkDetail(
      @PathVariable Long personalArtworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    PersonalArtworkResult result =
        personalArtworkQueryService.getPersonalArtworkDetail(
            personalArtworkId, optionalUserId(user));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @PatchMapping("/api/v1/personal-artworks/{personalArtworkId}")
  @Override
  public ApiResponseBody<PersonalArtworkResponse> updatePersonalArtwork(
      @PathVariable Long personalArtworkId,
      @RequestBody PersonalArtworkRequest personalArtworkRequest,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    personalArtworkCommandService.updatePersonalArtwork(
        personalArtworkId, requireUserId(user), mapper.toCommand(personalArtworkRequest));
    PersonalArtworkResult result =
        personalArtworkQueryService.getPersonalArtworkDetail(
            personalArtworkId, requireUserId(user));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @DeleteMapping("/api/v1/personal-artworks/{personalArtworkId}")
  @Override
  public ApiResponseBody<Void> deletePersonalArtwork(
      @PathVariable Long personalArtworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    personalArtworkCommandService.deletePersonalArtwork(personalArtworkId, requireUserId(user));
    return ApiResponseBody.success(null, request);
  }

  @PostMapping("/api/v1/personal-artworks/{personalArtworkId}/like")
  @Override
  public ApiResponseBody<PersonalArtworkLikeResponse> likePersonalArtwork(
      @PathVariable Long personalArtworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    PersonalArtworkLikeResult result =
        personalArtworkLikeCommandService.like(
            new PersonalArtworkLikeCommand(personalArtworkId, requireUserId(user)));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @DeleteMapping("/api/v1/personal-artworks/{personalArtworkId}/like")
  @Override
  public ApiResponseBody<PersonalArtworkLikeResponse> cancelPersonalArtworkLike(
      @PathVariable Long personalArtworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    PersonalArtworkLikeResult result =
        personalArtworkLikeCommandService.cancel(
            new PersonalArtworkLikeCommand(personalArtworkId, requireUserId(user)));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  // 인증이 필수인 API에서 사용한다. SecurityConfig가 모든 요청을 permitAll로 통과시키므로
  // 토큰이 없거나 유효하지 않으면 AuthUser가 null로 주입될 수 있어 컨트롤러단에서 막는다.
  private Long optionalUserId(AuthUser user) {
    return user == null ? null : user.userId();
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
