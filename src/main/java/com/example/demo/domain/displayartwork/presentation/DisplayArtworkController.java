package com.example.demo.domain.displayartwork.presentation;

import com.example.demo.domain.displayartwork.application.command.CreateDisplayArtworkService;
import com.example.demo.domain.displayartwork.application.query.DisplayArtworkQueryService;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkResult;
import com.example.demo.domain.displayartwork.presentation.mapper.DisplayArtworkPresentationMapper;
import com.example.demo.domain.displayartwork.presentation.request.CreateDisplayArtworkRequest;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "DisplayArtwork", description = "전시 출품작 API")
public class DisplayArtworkController {

  // TODO: 인증 붙기 전까지 사용하는 임시 사용자 ID. 로그인 구현되면 인증 정보에서 꺼내오도록 교체해야 함.
  private static final Long TEMP_USER_ID = 1L;

  private final CreateDisplayArtworkService createDisplayArtworkService;
  private final DisplayArtworkQueryService displayArtworkQueryService;
  private final DisplayArtworkPresentationMapper mapper;

  public DisplayArtworkController(
      CreateDisplayArtworkService createDisplayArtworkService,
      DisplayArtworkQueryService displayArtworkQueryService,
      DisplayArtworkPresentationMapper mapper) {
    this.createDisplayArtworkService = createDisplayArtworkService;
    this.displayArtworkQueryService = displayArtworkQueryService;
    this.mapper = mapper;
  }

  @PostMapping("/api/v1/artworks")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "전시 출품작 등록", description = "전시 팀원이 전시에 작품을 등록합니다.")
  public ApiResponseBody<DisplayArtworkResponse> createDisplayArtwork(
      @Valid @RequestBody CreateDisplayArtworkRequest request, HttpServletRequest httpRequest) {
    Long displayArtworkId =
        createDisplayArtworkService.createDisplayArtwork(TEMP_USER_ID, request.toCommand());
    DisplayArtworkResult result =
        displayArtworkQueryService.getDisplayArtworkDetail(displayArtworkId);
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }
}
