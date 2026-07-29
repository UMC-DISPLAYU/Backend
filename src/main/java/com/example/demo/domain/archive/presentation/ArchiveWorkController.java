package com.example.demo.domain.archive.presentation;

import com.example.demo.domain.archive.application.command.DeleteArchiveWorkService;
import com.example.demo.domain.archive.application.command.SaveArchiveWorkCommand;
import com.example.demo.domain.archive.application.command.SaveArchiveWorkService;
import com.example.demo.domain.archive.application.query.GetArchiveWorkDetailService;
import com.example.demo.domain.archive.application.query.GetArchivedWorksService;
import com.example.demo.domain.archive.application.result.ArchiveWorkCursorResult;
import com.example.demo.domain.archive.application.result.ArchiveWorkResult;
import com.example.demo.domain.archive.application.result.ArchiveWorkToggleResult;
import com.example.demo.domain.archive.presentation.docs.ArchiveWorkControllerDocs;
import com.example.demo.domain.archive.presentation.mapper.ArchivePresentationMapper;
import com.example.demo.domain.archive.presentation.response.ArchiveWorkCursorResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveWorkResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveWorkToggleResponse;
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
public class ArchiveWorkController implements ArchiveWorkControllerDocs {

  private final SaveArchiveWorkService saveArchiveWorkService;
  private final DeleteArchiveWorkService deleteArchiveWorkService;
  private final GetArchiveWorkDetailService getArchiveWorkDetailService;
  private final GetArchivedWorksService getArchivedWorksService;
  private final ArchivePresentationMapper mapper;

  public ArchiveWorkController(
      SaveArchiveWorkService saveArchiveWorkService,
      DeleteArchiveWorkService deleteArchiveWorkService,
      GetArchiveWorkDetailService getArchiveWorkDetailService,
      GetArchivedWorksService getArchivedWorksService,
      ArchivePresentationMapper mapper) {
    this.saveArchiveWorkService = saveArchiveWorkService;
    this.deleteArchiveWorkService = deleteArchiveWorkService;
    this.getArchiveWorkDetailService = getArchiveWorkDetailService;
    this.getArchivedWorksService = getArchivedWorksService;
    this.mapper = mapper;
  }

  @PostMapping("/api/v1/archives/artworks/{artworkId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Override
  public ApiResponseBody<ArchiveWorkToggleResponse> saveArchiveWork(
      @PathVariable Long artworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    ArchiveWorkToggleResult result =
        saveArchiveWorkService.saveArchiveWork(
            new SaveArchiveWorkCommand(requireUserId(user), artworkId));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @DeleteMapping("/api/v1/archives/artworks/{artworkId}")
  @Override
  public ApiResponseBody<ArchiveWorkToggleResponse> deleteArchiveWork(
      @PathVariable Long artworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    ArchiveWorkToggleResult result =
        deleteArchiveWorkService.deleteArchiveWork(requireUserId(user), artworkId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/archives/artworks/{savedArtworkId}")
  @Override
  public ApiResponseBody<ArchiveWorkResponse> getArchiveWorkDetail(
      @PathVariable Long savedArtworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    ArchiveWorkResult result =
        getArchiveWorkDetailService.getArchiveWorkDetail(requireUserId(user), savedArtworkId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/archives/artworks")
  @Override
  public ApiResponseBody<ArchiveWorkCursorResponse> getArchivedWorks(
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    ArchiveWorkCursorResult result =
        getArchivedWorksService.getArchivedWorks(requireUserId(user), cursorId, size);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
