package com.example.demo.domain.archive.presentation;

import com.example.demo.domain.archive.application.command.DeleteArchiveDisplayService;
import com.example.demo.domain.archive.application.command.SaveArchiveDisplayCommand;
import com.example.demo.domain.archive.application.command.SaveArchiveDisplayService;
import com.example.demo.domain.archive.application.query.GetArchiveDisplayDetailService;
import com.example.demo.domain.archive.application.query.GetArchivedDisplaysService;
import com.example.demo.domain.archive.application.result.ArchiveDisplayResult;
import com.example.demo.domain.archive.application.result.ArchiveDisplayToggleResult;
import com.example.demo.domain.archive.presentation.mapper.ArchivePresentationMapper;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayToggleResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Archive", description = "개인 아카이브 API")
public class ArchiveDisplayController {

  // TODO: 인증 붙기 전까지 사용하는 임시 사용자 ID. 로그인 구현되면 인증 정보에서 꺼내오도록 교체해야 함.
  private static final Long TEMP_USER_ID = 1L;

  private final SaveArchiveDisplayService saveArchiveDisplayService;
  private final DeleteArchiveDisplayService deleteArchiveDisplayService;
  private final GetArchiveDisplayDetailService getArchiveDisplayDetailService;
  private final GetArchivedDisplaysService getArchivedDisplaysService;
  private final ArchivePresentationMapper mapper;

  public ArchiveDisplayController(
      SaveArchiveDisplayService saveArchiveDisplayService,
      DeleteArchiveDisplayService deleteArchiveDisplayService,
      GetArchiveDisplayDetailService getArchiveDisplayDetailService,
      GetArchivedDisplaysService getArchivedDisplaysService,
      ArchivePresentationMapper mapper) {
    this.saveArchiveDisplayService = saveArchiveDisplayService;
    this.deleteArchiveDisplayService = deleteArchiveDisplayService;
    this.getArchiveDisplayDetailService = getArchiveDisplayDetailService;
    this.getArchivedDisplaysService = getArchivedDisplaysService;
    this.mapper = mapper;
  }

  @PostMapping("/api/v1/archives/exhibitions/{exhibitionId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "전시 저장", description = "전시를 개인 아카이브에 저장합니다.")
  public ApiResponseBody<ArchiveDisplayToggleResponse> saveArchiveDisplay(
      @Parameter(description = "전시 ID", example = "1") @PathVariable Long exhibitionId,
      HttpServletRequest request) {
    ArchiveDisplayToggleResult result =
        saveArchiveDisplayService.saveArchiveDisplay(
            new SaveArchiveDisplayCommand(TEMP_USER_ID, exhibitionId));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @DeleteMapping("/api/v1/archives/exhibitions/{exhibitionId}")
  @Operation(summary = "전시 저장 취소", description = "개인 아카이브에서 전시 저장을 취소합니다.")
  public ApiResponseBody<ArchiveDisplayToggleResponse> deleteArchiveDisplay(
      @Parameter(description = "전시 ID", example = "1") @PathVariable Long exhibitionId,
      HttpServletRequest request) {
    ArchiveDisplayToggleResult result =
        deleteArchiveDisplayService.deleteArchiveDisplay(TEMP_USER_ID, exhibitionId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/archives/exhibitions/{savedExhibitionId}")
  @Operation(summary = "저장된 전시 상세 조회", description = "저장 기록 ID로 저장된 전시 상세를 조회합니다.")
  public ApiResponseBody<ArchiveDisplayResponse> getArchiveDisplayDetail(
      @Parameter(description = "저장된 전시(아카이브 기록) ID", example = "1") @PathVariable
          Long savedExhibitionId,
      HttpServletRequest request) {
    ArchiveDisplayResult result =
        getArchiveDisplayDetailService.getArchiveDisplayDetail(savedExhibitionId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/archives/exhibitions")
  @Operation(summary = "저장된 전시 목록 조회", description = "내가 저장한 전시 목록을 최근 저장한 순으로 조회합니다.")
  public ApiResponseBody<List<ArchiveDisplayResponse>> getArchivedDisplays(
      HttpServletRequest request) {
    List<ArchiveDisplayResult> results =
        getArchivedDisplaysService.getArchivedDisplays(TEMP_USER_ID);
    return ApiResponseBody.success(results.stream().map(mapper::toResponse).toList(), request);
  }
}
