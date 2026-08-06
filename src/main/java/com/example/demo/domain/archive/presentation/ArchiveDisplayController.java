package com.example.demo.domain.archive.presentation;

import com.example.demo.domain.archive.application.command.DeleteArchiveDisplayService;
import com.example.demo.domain.archive.application.command.SaveArchiveDisplayCommand;
import com.example.demo.domain.archive.application.command.SaveArchiveDisplayService;
import com.example.demo.domain.archive.application.query.GetArchivedDisplaysService;
import com.example.demo.domain.archive.application.result.ArchiveDisplayCursorResult;
import com.example.demo.domain.archive.application.result.ArchiveDisplayToggleResult;
import com.example.demo.domain.archive.presentation.docs.ArchiveDisplayControllerDocs;
import com.example.demo.domain.archive.presentation.mapper.ArchivePresentationMapper;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayCursorResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveDisplayToggleResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

  private final SaveArchiveDisplayService saveArchiveDisplayService;
  private final DeleteArchiveDisplayService deleteArchiveDisplayService;
  private final GetArchivedDisplaysService getArchivedDisplaysService;
  private final ArchivePresentationMapper mapper;

  public ArchiveDisplayController(
      SaveArchiveDisplayService saveArchiveDisplayService,
      DeleteArchiveDisplayService deleteArchiveDisplayService,
      GetArchivedDisplaysService getArchivedDisplaysService,
      ArchivePresentationMapper mapper) {
    this.saveArchiveDisplayService = saveArchiveDisplayService;
    this.deleteArchiveDisplayService = deleteArchiveDisplayService;
    this.getArchivedDisplaysService = getArchivedDisplaysService;
    this.mapper = mapper;
  }

  @PostMapping("/api/v1/archives/exhibitions/{exhibitionId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Override
  public ApiResponseBody<ArchiveDisplayToggleResponse> saveArchiveDisplay(
      @PathVariable Long exhibitionId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    ArchiveDisplayToggleResult result =
        saveArchiveDisplayService.saveArchiveDisplay(
            new SaveArchiveDisplayCommand(requireUserId(user), exhibitionId));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @DeleteMapping("/api/v1/archives/exhibitions/{exhibitionId}")
  @Override
  public ApiResponseBody<ArchiveDisplayToggleResponse> deleteArchiveDisplay(
      @PathVariable Long exhibitionId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    ArchiveDisplayToggleResult result =
        deleteArchiveDisplayService.deleteArchiveDisplay(requireUserId(user), exhibitionId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/archives/exhibitions")
  @Override
  public ApiResponseBody<ArchiveDisplayCursorResponse> getArchivedDisplays(
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    ArchiveDisplayCursorResult result =
        getArchivedDisplaysService.getArchivedDisplays(requireUserId(user), cursorId, size);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
