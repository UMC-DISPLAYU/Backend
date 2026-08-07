package com.example.demo.domain.personalartworkcommunication.presentation;

import com.example.demo.domain.personalartworkcommunication.application.command.*;
import com.example.demo.domain.personalartworkcommunication.application.query.GetPersonalArtworkFeelingRepliesService;
import com.example.demo.domain.personalartworkcommunication.application.query.GetPersonalArtworkFeelingsService;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkFeelingReplyResult;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkFeelingResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingLikeResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyLikeResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyListResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingResult;
import com.example.demo.domain.personalartworkcommunication.presentation.docs.PersonalArtworkFeelingApiDocs;
import com.example.demo.domain.personalartworkcommunication.presentation.mapper.PersonalArtworkFeelingPresentationMapper;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkFeelingReplyRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkFeelingRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkFeelingReplyResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkFeelingResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingLikeResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingListResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingReplyLikeResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingReplyListResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingReplyResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/personal-artworks/{personalArtworkId}/feelings")
public class PersonalArtworkFeelingController implements PersonalArtworkFeelingApiDocs {

  private final PersonalArtworkFeelingService createPersonalArtworkFeelingService;
  private final DeletePersonalArtworkFeelingService deletePersonalArtworkFeelingService;
  private final DeletePersonalArtworkFeelingReplyService deletePersonalArtworkFeelingReplyService;
  private final PersonalArtworkFeelingReplyService personalArtworkFeelingReplyService;
  private final PersonalArtworkFeelingLikeService personalArtworkFeelingLikeService;
  private final PersonalArtworkFeelingReplyLikeService personalArtworkFeelingReplyLikeService;
  private final GetPersonalArtworkFeelingsService getPersonalArtworkFeelingsService;
  private final GetPersonalArtworkFeelingRepliesService getPersonalArtworkFeelingRepliesService;
  private final PersonalArtworkFeelingPresentationMapper mapper;

  @Override
  @GetMapping
  // 개인 작품 감상평 목록 조회
  public ApiResponseBody<PersonalArtworkFeelingListResponse> getFeelings(
      @PathVariable Long personalArtworkId,
      @RequestParam(required = false) @Positive Long cursorId,
      HttpServletRequest httpServletRequest) {
    PersonalArtworkFeelingListResult result =
        getPersonalArtworkFeelingsService.getFeelings(mapper.toQuery(personalArtworkId, cursorId));

    PersonalArtworkFeelingListResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @GetMapping("/{personalFeelingId}/replies")
  // 개인 작품 감상평 답변 목록 조회
  public ApiResponseBody<PersonalArtworkFeelingReplyListResponse> getFeelingReplies(
      @PathVariable Long personalArtworkId,
      @PathVariable Long personalFeelingId,
      @RequestParam(required = false) @Positive Long cursorId,
      HttpServletRequest httpServletRequest) {
    PersonalArtworkFeelingReplyListResult result =
        getPersonalArtworkFeelingRepliesService.getReplies(
            mapper.toRepliesQuery(personalArtworkId, personalFeelingId, cursorId));

    return ApiResponseBody.success(mapper.toResponse(result), httpServletRequest);
  }

  @Override
  @PostMapping
  @SecurityRequirement(name = "Authorization")
  // 개인 작품 감상평 작성
  public ApiResponseBody<PersonalArtworkFeelingResponse> createPersonalFeeling(
      @PathVariable Long personalArtworkId,
      @AuthenticationPrincipal AuthUser user,
      @Valid @RequestBody CreatePersonalArtworkFeelingRequest request,
      HttpServletRequest httpServletRequest) {
    PersonalArtworkFeelingCommand command =
        mapper.toCommand(personalArtworkId, requireUserId(user), request);

    PersonalArtworkFeelingResult result =
        createPersonalArtworkFeelingService.createPersonalFeeling(command);

    PersonalArtworkFeelingResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{personalFeelingId}/replies")
  @SecurityRequirement(name = "Authorization")
  // 개인 작품 감상평 답변 등록
  public ApiResponseBody<PersonalArtworkFeelingReplyResponse> createFeelingReply(
      @PathVariable Long personalArtworkId,
      @PathVariable Long personalFeelingId,
      @AuthenticationPrincipal AuthUser user,
      @Valid @RequestBody CreatePersonalArtworkFeelingReplyRequest request,
      HttpServletRequest httpServletRequest) {
    PersonalArtworkFeelingReplyCommand command =
        mapper.toCommand(personalArtworkId, personalFeelingId, requireUserId(user), request);

    PersonalArtworkFeelingReplyResult result =
        personalArtworkFeelingReplyService.createFeelingReply(command);

    PersonalArtworkFeelingReplyResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @DeleteMapping("/{personalFeelingId}/replies/{personalFeelingReplyId}")
  @SecurityRequirement(name = "Authorization")
  // 개인 작품 감상평 답변 삭제
  public ApiResponseBody<DeletedPersonalArtworkFeelingReplyResponse> deleteFeelingReply(
      @PathVariable Long personalArtworkId,
      @PathVariable Long personalFeelingId,
      @PathVariable Long personalFeelingReplyId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    DeletePersonalArtworkFeelingReplyCommand command =
        new DeletePersonalArtworkFeelingReplyCommand(
            personalArtworkId, personalFeelingId, personalFeelingReplyId, requireUserId(user));

    DeletedPersonalArtworkFeelingReplyResult result =
        deletePersonalArtworkFeelingReplyService.deleteReply(command);

    DeletedPersonalArtworkFeelingReplyResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @DeleteMapping("/{personalFeelingId}")
  @SecurityRequirement(name = "Authorization")
  // 개인 작품 감상평 삭제
  public ApiResponseBody<DeletedPersonalArtworkFeelingResponse> deleteFeeling(
      @PathVariable Long personalArtworkId,
      @PathVariable Long personalFeelingId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    DeletePersonalArtworkFeelingCommand command =
        new DeletePersonalArtworkFeelingCommand(
            personalArtworkId, personalFeelingId, requireUserId(user));

    DeletedPersonalArtworkFeelingResult result =
        deletePersonalArtworkFeelingService.deleteFeeling(command);

    DeletedPersonalArtworkFeelingResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{personalFeelingId}/like")
  @SecurityRequirement(name = "Authorization")
  // 개인 작품 감상평 좋아요 등록 및 취소
  public ApiResponseBody<PersonalArtworkFeelingLikeResponse> feelingLike(
      @PathVariable Long personalArtworkId,
      @PathVariable Long personalFeelingId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    PersonalArtworkFeelingLikeCommand command =
        new PersonalArtworkFeelingLikeCommand(
            personalArtworkId, personalFeelingId, requireUserId(user));

    PersonalArtworkFeelingLikeResult result =
        personalArtworkFeelingLikeService.toggleFeelingLike(command);

    PersonalArtworkFeelingLikeResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{personalFeelingId}/replies/{personalFeelingReplyId}/like")
  @SecurityRequirement(name = "Authorization")
  // 개인 작품 감상평 답변 좋아요 등록 및 취소
  public ApiResponseBody<PersonalArtworkFeelingReplyLikeResponse> feelingReplyLike(
      @PathVariable Long personalArtworkId,
      @PathVariable Long personalFeelingId,
      @PathVariable Long personalFeelingReplyId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    PersonalArtworkFeelingReplyLikeCommand command =
        new PersonalArtworkFeelingReplyLikeCommand(
            personalArtworkId, personalFeelingId, personalFeelingReplyId, requireUserId(user));

    PersonalArtworkFeelingReplyLikeResult result =
        personalArtworkFeelingReplyLikeService.toggleReplyLike(command);

    PersonalArtworkFeelingReplyLikeResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
