package com.example.demo.domain.personalartworkcommunication.presentation;

import com.example.demo.domain.personalartworkcommunication.application.command.CreatePersonalArtworkFeelingCommand;
import com.example.demo.domain.personalartworkcommunication.application.command.CreatePersonalArtworkFeelingService;
import com.example.demo.domain.personalartworkcommunication.application.command.DeletePersonalArtworkFeelingCommand;
import com.example.demo.domain.personalartworkcommunication.application.command.DeletePersonalArtworkFeelingService;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkFeelingResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingResult;
import com.example.demo.domain.personalartworkcommunication.presentation.docs.PersonalArtworkFeelingApiDocs;
import com.example.demo.domain.personalartworkcommunication.presentation.mapper.PersonalArtworkFeelingPresentationMapper;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkFeelingRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkFeelingResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/personal-artworks/{personalArtworkId}/feelings")
public class PersonalArtworkFeelingController implements PersonalArtworkFeelingApiDocs {

  private final CreatePersonalArtworkFeelingService createPersonalArtworkFeelingService;
  private final DeletePersonalArtworkFeelingService deletePersonalArtworkFeelingService;
  private final PersonalArtworkFeelingPresentationMapper mapper;

  @Override
  @PostMapping
  // 개인 작품 감상평 작성
  public ApiResponseBody<PersonalArtworkFeelingResponse> createPersonalFeeling(
      @PathVariable Long personalArtworkId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      @Valid @RequestBody CreatePersonalArtworkFeelingRequest request,
      HttpServletRequest httpServletRequest) {
    CreatePersonalArtworkFeelingCommand command =
        mapper.toCommand(personalArtworkId, userId, request);

    PersonalArtworkFeelingResult result =
        createPersonalArtworkFeelingService.createPersonalFeeling(command);

    PersonalArtworkFeelingResponse response = mapper.toResponse(result);

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
}
