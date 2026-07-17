package com.example.demo.domain.archive.presentation;

import com.example.demo.domain.archive.application.command.DeleteArchiveArtistService;
import com.example.demo.domain.archive.application.command.SaveArchiveArtistCommand;
import com.example.demo.domain.archive.application.command.SaveArchiveArtistService;
import com.example.demo.domain.archive.application.query.GetArchiveArtistDetailService;
import com.example.demo.domain.archive.application.query.GetArchivedArtistsService;
import com.example.demo.domain.archive.application.result.ArchiveArtistCursorResult;
import com.example.demo.domain.archive.application.result.ArchiveArtistResult;
import com.example.demo.domain.archive.application.result.ArchiveArtistToggleResult;
import com.example.demo.domain.archive.presentation.docs.ArchiveArtistControllerDocs;
import com.example.demo.domain.archive.presentation.mapper.ArchivePresentationMapper;
import com.example.demo.domain.archive.presentation.response.ArchiveArtistCursorResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveArtistResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveArtistToggleResponse;
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
public class ArchiveArtistController implements ArchiveArtistControllerDocs {

  // TODO: 인증 붙기 전까지 사용하는 임시 사용자 ID. 로그인 구현되면 인증 정보에서 꺼내오도록 교체해야 함.
  private static final Long TEMP_USER_ID = 1L;

  private final SaveArchiveArtistService saveArchiveArtistService;
  private final DeleteArchiveArtistService deleteArchiveArtistService;
  private final GetArchiveArtistDetailService getArchiveArtistDetailService;
  private final GetArchivedArtistsService getArchivedArtistsService;
  private final ArchivePresentationMapper mapper;

  public ArchiveArtistController(
      SaveArchiveArtistService saveArchiveArtistService,
      DeleteArchiveArtistService deleteArchiveArtistService,
      GetArchiveArtistDetailService getArchiveArtistDetailService,
      GetArchivedArtistsService getArchivedArtistsService,
      ArchivePresentationMapper mapper) {
    this.saveArchiveArtistService = saveArchiveArtistService;
    this.deleteArchiveArtistService = deleteArchiveArtistService;
    this.getArchiveArtistDetailService = getArchiveArtistDetailService;
    this.getArchivedArtistsService = getArchivedArtistsService;
    this.mapper = mapper;
  }

  @PostMapping("/api/v1/archives/artists/{artistId}")
  @ResponseStatus(HttpStatus.CREATED)
  @Override
  public ApiResponseBody<ArchiveArtistToggleResponse> saveArchiveArtist(
      @PathVariable Long artistId, HttpServletRequest request) {
    ArchiveArtistToggleResult result =
        saveArchiveArtistService.saveArchiveArtist(
            new SaveArchiveArtistCommand(TEMP_USER_ID, artistId));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @DeleteMapping("/api/v1/archives/artists/{artistId}")
  @Override
  public ApiResponseBody<ArchiveArtistToggleResponse> deleteArchiveArtist(
      @PathVariable Long artistId, HttpServletRequest request) {
    ArchiveArtistToggleResult result =
        deleteArchiveArtistService.deleteArchiveArtist(TEMP_USER_ID, artistId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/archives/artists/{savedArtistId}")
  @Override
  public ApiResponseBody<ArchiveArtistResponse> getArchiveArtistDetail(
      @PathVariable Long savedArtistId, HttpServletRequest request) {
    ArchiveArtistResult result =
        getArchiveArtistDetailService.getArchiveArtistDetail(TEMP_USER_ID, savedArtistId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/archives/artists")
  @Override
  public ApiResponseBody<ArchiveArtistCursorResponse> getArchivedArtists(
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      HttpServletRequest request) {
    ArchiveArtistCursorResult result =
        getArchivedArtistsService.getArchivedArtists(TEMP_USER_ID, cursorId, size);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }
}
