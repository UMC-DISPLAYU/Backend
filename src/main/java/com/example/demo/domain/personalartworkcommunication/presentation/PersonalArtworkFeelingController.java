package com.example.demo.domain.personalartworkcommunication.presentation;

import com.example.demo.domain.personalartworkcommunication.application.command.*;
import com.example.demo.domain.personalartworkcommunication.application.query.GetPersonalArtworkFeelingsService;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkFeelingResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingLikeResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingResult;
import com.example.demo.domain.personalartworkcommunication.presentation.docs.PersonalArtworkFeelingApiDocs;
import com.example.demo.domain.personalartworkcommunication.presentation.mapper.PersonalArtworkFeelingPresentationMapper;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkFeelingReplyRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkFeelingRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkFeelingResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingLikeResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingListResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingReplyResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/personal-artworks/{personalArtworkId}/feelings")
public class PersonalArtworkFeelingController implements PersonalArtworkFeelingApiDocs {

  private final PersonalArtworkFeelingService createPersonalArtworkFeelingService;
  private final DeletePersonalArtworkFeelingService deletePersonalArtworkFeelingService;
  private final PersonalArtworkFeelingReplyService personalArtworkFeelingReplyService;
  private final PersonalArtworkFeelingLikeService personalArtworkFeelingLikeService;
  private final GetPersonalArtworkFeelingsService getPersonalArtworkFeelingsService;
  private final PersonalArtworkFeelingPresentationMapper mapper;

  @Override
  @GetMapping
  // 개인 작품 감상평 목록 및 답변 조회
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
  @PostMapping
  // 개인 작품 감상평 작성
  public ApiResponseBody<PersonalArtworkFeelingResponse> createPersonalFeeling(
      @PathVariable Long personalArtworkId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      @Valid @RequestBody CreatePersonalArtworkFeelingRequest request,
      HttpServletRequest httpServletRequest) {
    PersonalArtworkFeelingCommand command = mapper.toCommand(personalArtworkId, userId, request);

    PersonalArtworkFeelingResult result =
        createPersonalArtworkFeelingService.createPersonalFeeling(command);

    PersonalArtworkFeelingResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{personalFeelingId}/reply")
  // 개인 작품 감상평 답변 등록
  public ApiResponseBody<PersonalArtworkFeelingReplyResponse> createFeelingReply(
      @PathVariable Long personalArtworkId,
      @PathVariable Long personalFeelingId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      @Valid @RequestBody CreatePersonalArtworkFeelingReplyRequest request,
      HttpServletRequest httpServletRequest) {
    PersonalArtworkFeelingReplyCommand command =
        mapper.toCommand(personalArtworkId, personalFeelingId, userId, request);

    PersonalArtworkFeelingReplyResult result =
        personalArtworkFeelingReplyService.createFeelingReply(command);

    PersonalArtworkFeelingReplyResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @DeleteMapping("/{personalFeelingId}")
  // 개인 작품 감상평 삭제
  public ApiResponseBody<DeletedPersonalArtworkFeelingResponse> deleteFeeling(
      @PathVariable Long personalArtworkId,
      @PathVariable Long personalFeelingId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      HttpServletRequest httpServletRequest) {
    DeletePersonalArtworkFeelingCommand command =
        new DeletePersonalArtworkFeelingCommand(personalArtworkId, personalFeelingId, userId);

    DeletedPersonalArtworkFeelingResult result =
        deletePersonalArtworkFeelingService.deleteFeeling(command);

    DeletedPersonalArtworkFeelingResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{personalFeelingId}/like")
  // 개인 작품 감상평 좋아요 등록 및 취소
  public ApiResponseBody<PersonalArtworkFeelingLikeResponse> feelingLike(
      @PathVariable Long personalArtworkId,
      @PathVariable Long personalFeelingId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      HttpServletRequest httpServletRequest) {
    PersonalArtworkFeelingLikeCommand command =
        new PersonalArtworkFeelingLikeCommand(personalArtworkId, personalFeelingId, userId);

    PersonalArtworkFeelingLikeResult result =
        personalArtworkFeelingLikeService.toggleFeelingLike(command);

    PersonalArtworkFeelingLikeResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }
}
