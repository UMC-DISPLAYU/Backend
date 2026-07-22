package com.example.demo.domain.archive.presentation;

import com.example.demo.domain.archive.application.command.DeleteArchiveDisplayService;
import com.example.demo.domain.archive.application.command.SaveArchiveDisplayCommand;
import com.example.demo.domain.archive.application.command.SaveArchiveDisplayService;
import com.example.demo.domain.archive.application.query.GetArchiveDisplayDetailService;
import com.example.demo.domain.archive.application.query.GetArchivedDisplaysService;
import com.example.demo.domain.archive.application.result.ArchiveDisplayCursorResult;
import com.example.demo.domain.archive.application.result.ArchiveDisplayResult;
import com.example.demo.domain.archive.application.result.ArchiveDisplayToggleResult;
import com.example.demo.domain.archive.presentation.docs.ArchiveDisplayControllerDocs;
import com.example.demo.domain.archive.presentation.mapper.ArchivePresentationMapper;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayCursorResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayToggleResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class ArchiveDisplayController implements ArchiveDisplayControllerDocs {

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
  @Override
  public ApiResponseBody<ArchiveDisplayToggleResponse> saveArchiveDisplay(
      @PathVariable Long exhibitionId, HttpServletRequest request) {
    ArchiveDisplayToggleResult result =
        saveArchiveDisplayService.saveArchiveDisplay(
            new SaveArchiveDisplayCommand(TEMP_USER_ID, exhibitionId));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @DeleteMapping("/api/v1/archives/exhibitions/{exhibitionId}")
  @Override
  public ApiResponseBody<ArchiveDisplayToggleResponse> deleteArchiveDisplay(
      @PathVariable Long exhibitionId, HttpServletRequest request) {
    ArchiveDisplayToggleResult result =
        deleteArchiveDisplayService.deleteArchiveDisplay(TEMP_USER_ID, exhibitionId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/archives/exhibitions/{savedExhibitionId}")
  @Override
  public ApiResponseBody<ArchiveDisplayResponse> getArchiveDisplayDetail(
      @PathVariable Long savedExhibitionId, HttpServletRequest request) {
    ArchiveDisplayResult result =
        getArchiveDisplayDetailService.getArchiveDisplayDetail(TEMP_USER_ID, savedExhibitionId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/archives/exhibitions")
  @Override
  public ApiResponseBody<ArchiveDisplayCursorResponse> getArchivedDisplays(
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      HttpServletRequest request) {
    ArchiveDisplayCursorResult result =
        getArchivedDisplaysService.getArchivedDisplays(TEMP_USER_ID, cursorId, size);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }
}
