package com.example.demo.domain.archive.presentation;

import com.example.demo.domain.archive.application.command.DeleteArchivePersonalWorkService;
import com.example.demo.domain.archive.application.command.SaveArchivePersonalWorkCommand;
import com.example.demo.domain.archive.application.command.SaveArchivePersonalWorkService;
import com.example.demo.domain.archive.application.result.ArchivePersonalWorkToggleResult;
import com.example.demo.domain.archive.presentation.docs.ArchivePersonalWorkControllerDocs;
import com.example.demo.domain.archive.presentation.mapper.ArchivePresentationMapper;
import com.example.demo.domain.archive.presentation.response.ArchivePersonalWorkToggleResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class ArchivePersonalWorkController implements ArchivePersonalWorkControllerDocs {

  private final SaveArchivePersonalWorkService saveArchivePersonalWorkService;
  private final DeleteArchivePersonalWorkService deleteArchivePersonalWorkService;
  private final ArchivePresentationMapper mapper;

  public ArchivePersonalWorkController(
      SaveArchivePersonalWorkService saveArchivePersonalWorkService,
      DeleteArchivePersonalWorkService deleteArchivePersonalWorkService,
      ArchivePresentationMapper mapper) {
    this.saveArchivePersonalWorkService = saveArchivePersonalWorkService;
    this.deleteArchivePersonalWorkService = deleteArchivePersonalWorkService;
    this.mapper = mapper;
  }

  @PostMapping("/api/v1/archives/personal-artworks/{personalArtworkId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Override
  public ApiResponseBody<ArchivePersonalWorkToggleResponse> saveArchivePersonalWork(
      @PathVariable Long personalArtworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    ArchivePersonalWorkToggleResult result =
        saveArchivePersonalWorkService.saveArchivePersonalWork(
            new SaveArchivePersonalWorkCommand(requireUserId(user), personalArtworkId));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @DeleteMapping("/api/v1/archives/personal-artworks/{personalArtworkId}")
  @Override
  public ApiResponseBody<ArchivePersonalWorkToggleResponse> deleteArchivePersonalWork(
      @PathVariable Long personalArtworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    ArchivePersonalWorkToggleResult result =
        deleteArchivePersonalWorkService.deleteArchivePersonalWork(
            requireUserId(user), personalArtworkId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
