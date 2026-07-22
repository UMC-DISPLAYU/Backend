package com.example.demo.domain.personalartworkcommunication.presentation;

import com.example.demo.domain.personalartworkcommunication.application.command.CreatePersonalArtworkQuestionCommand;
import com.example.demo.domain.personalartworkcommunication.application.command.CreatePersonalArtworkQuestionService;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionResult;
import com.example.demo.domain.personalartworkcommunication.presentation.docs.PersonalArtworkQuestionApiDocs;
import com.example.demo.domain.personalartworkcommunication.presentation.mapper.PersonalArtworkQuestionPresentationMapper;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkQuestionRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/personal-artworks/{personalArtworkId}/questions")
public class PersonalArtworkQuestionController implements PersonalArtworkQuestionApiDocs {

  private final CreatePersonalArtworkQuestionService createPersonalArtworkQuestionService;
  private final PersonalArtworkQuestionPresentationMapper mapper;

  @Override
  @PostMapping
  // 개인 작품 질문 작성
  public ApiResponseBody<PersonalArtworkQuestionResponse> createPersonalQuestion(
      @PathVariable Long personalArtworkId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      @Valid @RequestBody CreatePersonalArtworkQuestionRequest request,
      HttpServletRequest httpServletRequest) {
    CreatePersonalArtworkQuestionCommand command =
        mapper.toCommand(personalArtworkId, userId, request);

    PersonalArtworkQuestionResult result =
        createPersonalArtworkQuestionService.createPersonalQuestion(command);

    PersonalArtworkQuestionResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }
}
