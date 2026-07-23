package com.example.demo.domain.personalartwork.presentation;

import com.example.demo.domain.personalartwork.application.command.PersonalArtworkCommandService;
import com.example.demo.domain.personalartwork.application.command.PersonalArtworkLikeCommand;
import com.example.demo.domain.personalartwork.application.command.PersonalArtworkLikeCommandService;
import com.example.demo.domain.personalartwork.application.query.PersonalArtworkQueryService;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkLikeResult;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkResult;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkSummaryResult;
import com.example.demo.domain.personalartwork.presentation.mapper.PersonalArtworkPresentationMapper;
import com.example.demo.domain.personalartwork.presentation.request.PersonalArtworkRequest;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkLikeResponse;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkResponse;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkSummaryResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
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
@Tag(name = "PersonalArtwork", description = "개인 작품(작가 프로필 아카이브) API")
public class PersonalArtworkController {

  // TODO: 인증 붙기 전까지 사용하는 임시 사용자 ID. JWT 인증 구현되면 인증 정보에서 꺼내오도록 교체해야 함.
  private static final Long TEMP_USER_ID = 1L;

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
  @Operation(summary = "개인 작품 등록", description = "작가 프로필에 전시와 무관한 개인 작품을 등록합니다.")
  public ApiResponseBody<PersonalArtworkResponse> createPersonalArtwork(
      @Valid @RequestBody PersonalArtworkRequest personalArtworkRequest,
      HttpServletRequest request) {
    Long personalArtworkId =
        personalArtworkCommandService.createPersonalArtwork(
            TEMP_USER_ID, personalArtworkRequest.toCommand());
    PersonalArtworkResult result =
        personalArtworkQueryService.getPersonalArtworkDetail(personalArtworkId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/personal-artworks")
  @Operation(
      summary = "개인 작품 목록 조회",
      description = "특정 유저(작가 프로필)의 개인 작품 목록을 가볍게 조회합니다 (작품탭 카드용). 본인/타인 조회 공용입니다.")
  public ApiResponseBody<List<PersonalArtworkSummaryResponse>> getPersonalArtworks(
      @Parameter(description = "조회할 작가 프로필의 유저 ID", example = "1") @RequestParam Long userId,
      HttpServletRequest request) {
    List<PersonalArtworkSummaryResult> results =
        personalArtworkQueryService.getPersonalArtworksByUser(userId);
    return ApiResponseBody.success(results.stream().map(mapper::toResponse).toList(), request);
  }

  @GetMapping("/api/v1/personal-artworks/{personalArtworkId}")
  @Operation(summary = "개인 작품 단건 상세 조회", description = "수정 화면 진입 시 본인 소유 개인 작품의 전체 필드를 조회합니다.")
  public ApiResponseBody<PersonalArtworkResponse> getPersonalArtworkDetail(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable Long personalArtworkId,
      HttpServletRequest request) {
    PersonalArtworkResult result =
        personalArtworkQueryService.getOwnedPersonalArtworkDetail(personalArtworkId, TEMP_USER_ID);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @PatchMapping("/api/v1/personal-artworks/{personalArtworkId}")
  @Operation(summary = "개인 작품 수정", description = "본인이 등록한 개인 작품의 내용과 이미지를 수정합니다.")
  public ApiResponseBody<PersonalArtworkResponse> updatePersonalArtwork(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable Long personalArtworkId,
      @Valid @RequestBody PersonalArtworkRequest personalArtworkRequest,
      HttpServletRequest request) {
    personalArtworkCommandService.updatePersonalArtwork(
        personalArtworkId, TEMP_USER_ID, personalArtworkRequest.toCommand());
    PersonalArtworkResult result =
        personalArtworkQueryService.getPersonalArtworkDetail(personalArtworkId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @DeleteMapping("/api/v1/personal-artworks/{personalArtworkId}")
  @Operation(summary = "개인 작품 삭제", description = "본인이 등록한 개인 작품을 삭제합니다.")
  public ApiResponseBody<Void> deletePersonalArtwork(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable Long personalArtworkId,
      HttpServletRequest request) {
    personalArtworkCommandService.deletePersonalArtwork(personalArtworkId, TEMP_USER_ID);
    return ApiResponseBody.success(null, request);
  }

  @PostMapping("/api/v1/personal-artworks/{personalArtworkId}/like")
  @Operation(summary = "개인 작품 좋아요 등록", description = "개인 작품에 좋아요를 등록합니다.")
  public ApiResponseBody<PersonalArtworkLikeResponse> likePersonalArtwork(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable Long personalArtworkId,
      HttpServletRequest request) {
    PersonalArtworkLikeResult result =
        personalArtworkLikeCommandService.like(
            new PersonalArtworkLikeCommand(personalArtworkId, TEMP_USER_ID));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @DeleteMapping("/api/v1/personal-artworks/{personalArtworkId}/like")
  @Operation(summary = "개인 작품 좋아요 취소", description = "개인 작품 좋아요를 취소합니다.")
  public ApiResponseBody<PersonalArtworkLikeResponse> cancelPersonalArtworkLike(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable Long personalArtworkId,
      HttpServletRequest request) {
    PersonalArtworkLikeResult result =
        personalArtworkLikeCommandService.cancel(
            new PersonalArtworkLikeCommand(personalArtworkId, TEMP_USER_ID));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }
}
