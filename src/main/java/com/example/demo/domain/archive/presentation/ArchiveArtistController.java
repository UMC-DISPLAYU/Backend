package com.example.demo.domain.archive.presentation;

import com.example.demo.domain.archive.application.command.DeleteArchiveArtistService;
import com.example.demo.domain.archive.application.command.SaveArchiveArtistCommand;
import com.example.demo.domain.archive.application.command.SaveArchiveArtistService;
import com.example.demo.domain.archive.application.query.GetArchivedArtistsService;
import com.example.demo.domain.archive.application.result.ArchiveArtistCursorResult;
import com.example.demo.domain.archive.application.result.ArchiveArtistToggleResult;
import com.example.demo.domain.archive.presentation.docs.ArchiveArtistControllerDocs;
import com.example.demo.domain.archive.presentation.mapper.ArchivePresentationMapper;
import com.example.demo.domain.archive.presentation.response.ArchiveArtistCursorResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveArtistToggleResponse;
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
public class ArchiveArtistController implements ArchiveArtistControllerDocs {

  private final SaveArchiveArtistService saveArchiveArtistService;
  private final DeleteArchiveArtistService deleteArchiveArtistService;
  private final GetArchivedArtistsService getArchivedArtistsService;
  private final ArchivePresentationMapper mapper;

  public ArchiveArtistController(
      SaveArchiveArtistService saveArchiveArtistService,
      DeleteArchiveArtistService deleteArchiveArtistService,
      GetArchivedArtistsService getArchivedArtistsService,
      ArchivePresentationMapper mapper) {
    this.saveArchiveArtistService = saveArchiveArtistService;
    this.deleteArchiveArtistService = deleteArchiveArtistService;
    this.getArchivedArtistsService = getArchivedArtistsService;
    this.mapper = mapper;
  }

  @PostMapping("/api/v1/archives/artists/{artistId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Override
  public ApiResponseBody<ArchiveArtistToggleResponse> saveArchiveArtist(
      @PathVariable Long artistId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    ArchiveArtistToggleResult result =
        saveArchiveArtistService.saveArchiveArtist(
            new SaveArchiveArtistCommand(requireUserId(user), artistId));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @DeleteMapping("/api/v1/archives/artists/{artistId}")
  @Override
  public ApiResponseBody<ArchiveArtistToggleResponse> deleteArchiveArtist(
      @PathVariable Long artistId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    ArchiveArtistToggleResult result =
        deleteArchiveArtistService.deleteArchiveArtist(requireUserId(user), artistId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/archives/artists")
  @Override
  public ApiResponseBody<ArchiveArtistCursorResponse> getArchivedArtists(
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    ArchiveArtistCursorResult result =
        getArchivedArtistsService.getArchivedArtists(requireUserId(user), cursorId, size);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
