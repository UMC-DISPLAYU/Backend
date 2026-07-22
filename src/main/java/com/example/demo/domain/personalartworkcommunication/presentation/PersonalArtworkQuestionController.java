package com.example.demo.domain.personalartworkcommunication.presentation;

import com.example.demo.domain.personalartworkcommunication.application.command.DeletePersonalArtworkQuestionCommand;
import com.example.demo.domain.personalartworkcommunication.application.command.DeletePersonalArtworkQuestionService;
import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkQuestionCommand;
import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkQuestionService;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkQuestionResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionResult;
import com.example.demo.domain.personalartworkcommunication.presentation.docs.PersonalArtworkQuestionApiDocs;
import com.example.demo.domain.personalartworkcommunication.presentation.mapper.PersonalArtworkQuestionPresentationMapper;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkQuestionRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkQuestionResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/personal-artworks/{personalArtworkId}/questions")
public class PersonalArtworkQuestionController implements PersonalArtworkQuestionApiDocs {

  private final PersonalArtworkQuestionService createPersonalArtworkQuestionService;
  private final DeletePersonalArtworkQuestionService deletePersonalArtworkQuestionService;
  private final PersonalArtworkQuestionPresentationMapper mapper;

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
  @DeleteMapping("/{personalQuestionId}")
  // 개인 작품 질문 삭제
  public ApiResponseBody<DeletedPersonalArtworkQuestionResponse> deleteQuestion(
      @PathVariable Long personalArtworkId,
      @PathVariable Long personalQuestionId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      HttpServletRequest httpServletRequest) {
    DeletePersonalArtworkQuestionCommand command =
        new DeletePersonalArtworkQuestionCommand(personalArtworkId, personalQuestionId, userId);

    DeletedPersonalArtworkQuestionResult result =
        deletePersonalArtworkQuestionService.deleteQuestion(command);

    DeletedPersonalArtworkQuestionResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }
}
