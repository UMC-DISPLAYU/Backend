package com.example.demo.domain.personalartworkcommunication.presentation;

import com.example.demo.domain.personalartworkcommunication.application.command.*;
import com.example.demo.domain.personalartworkcommunication.application.query.GetPersonalArtworkQuestionsService;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkQuestionResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionListResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionReplyResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionResult;
import com.example.demo.domain.personalartworkcommunication.presentation.docs.PersonalArtworkQuestionApiDocs;
import com.example.demo.domain.personalartworkcommunication.presentation.mapper.PersonalArtworkQuestionPresentationMapper;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkQuestionReplyRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkQuestionRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkQuestionResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionListResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionReplyResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/personal-artworks/{personalArtworkId}/questions")
public class PersonalArtworkQuestionController implements PersonalArtworkQuestionApiDocs {

  private final PersonalArtworkQuestionService createPersonalArtworkQuestionService;
  private final DeletePersonalArtworkQuestionService deletePersonalArtworkQuestionService;
  private final PersonalArtworkQuestionReplyService personalArtworkQuestionReplyService;
  private final GetPersonalArtworkQuestionsService getPersonalArtworkQuestionsService;
  private final PersonalArtworkQuestionPresentationMapper mapper;

  @Override
  @GetMapping
  // 개인 작품 질문 목록 및 답변 조회
  public ApiResponseBody<PersonalArtworkQuestionListResponse> getQuestions(
      @PathVariable Long personalArtworkId,
      @RequestParam(required = false) @Positive Long cursorId,
      HttpServletRequest httpServletRequest) {
    PersonalArtworkQuestionListResult result =
        getPersonalArtworkQuestionsService.getQuestions(
            mapper.toQuery(personalArtworkId, cursorId));

    PersonalArtworkQuestionListResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping
  // 개인 작품 질문 작성
  public ApiResponseBody<PersonalArtworkQuestionResponse> createPersonalQuestion(
      @PathVariable Long personalArtworkId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      @Valid @RequestBody CreatePersonalArtworkQuestionRequest request,
      HttpServletRequest httpServletRequest) {
    PersonalArtworkQuestionCommand command = mapper.toCommand(personalArtworkId, userId, request);

    PersonalArtworkQuestionResult result =
        createPersonalArtworkQuestionService.createPersonalQuestion(command);

    PersonalArtworkQuestionResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{personalQuestionId}/reply")
  // 개인 작품 질문 답변 등록
  public ApiResponseBody<PersonalArtworkQuestionReplyResponse> createQuestionReply(
      @PathVariable Long personalArtworkId,
      @PathVariable Long personalQuestionId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      @Valid @RequestBody CreatePersonalArtworkQuestionReplyRequest request,
      HttpServletRequest httpServletRequest) {
    PersonalArtworkQuestionReplyCommand command =
        mapper.toCommand(personalArtworkId, personalQuestionId, userId, request);

    PersonalArtworkQuestionReplyResult result =
        personalArtworkQuestionReplyService.createQuestionReply(command);

    PersonalArtworkQuestionReplyResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @DeleteMapping("/{personalQuestionId}")
  // 개인 작품 질문 삭제
  public ApiResponseBody<DeletedPersonalArtworkQuestionResponse> deleteQuestion(
      @PathVariable Long personalArtworkId,
      @PathVariable Long personalQuestionId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      HttpServletRequest httpServletRequest) {
    DeletePersonalArtworkQuestionCommand command =
        mapper.toCommand(personalArtworkId, personalQuestionId, userId);

    DeletedPersonalArtworkQuestionResult result =
        deletePersonalArtworkQuestionService.deleteQuestion(command);

    DeletedPersonalArtworkQuestionResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }
}
