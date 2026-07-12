package com.example.demo.domain.archive.presentation;

import com.example.demo.domain.archive.application.command.DeleteArchiveWorkService;
import com.example.demo.domain.archive.application.command.SaveArchiveWorkCommand;
import com.example.demo.domain.archive.application.command.SaveArchiveWorkService;
import com.example.demo.domain.archive.application.query.GetArchiveWorkDetailService;
import com.example.demo.domain.archive.application.query.GetArchivedWorksService;
import com.example.demo.domain.archive.application.result.ArchiveWorkResult;
import com.example.demo.domain.archive.application.result.ArchiveWorkToggleResult;
import com.example.demo.domain.archive.presentation.docs.ArchiveWorkControllerDocs;
import com.example.demo.domain.archive.presentation.mapper.ArchivePresentationMapper;
import com.example.demo.domain.archive.presentation.response.ArchiveWorkResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveWorkToggleResponse;
import com.example.demo.global.response.ApiResponseBody;
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
public class ArchiveWorkController implements ArchiveWorkControllerDocs {

  // TODO: 인증 붙기 전까지 사용하는 임시 사용자 ID. 로그인 구현되면 인증 정보에서 꺼내오도록 교체해야 함.
  private static final Long TEMP_USER_ID = 1L;

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
      @PathVariable Long artworkId, HttpServletRequest request) {
    ArchiveWorkToggleResult result =
        saveArchiveWorkService.saveArchiveWork(new SaveArchiveWorkCommand(TEMP_USER_ID, artworkId));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @DeleteMapping("/api/v1/archives/artworks/{artworkId}")
  @Override
  public ApiResponseBody<ArchiveWorkToggleResponse> deleteArchiveWork(
      @PathVariable Long artworkId, HttpServletRequest request) {
    ArchiveWorkToggleResult result =
        deleteArchiveWorkService.deleteArchiveWork(TEMP_USER_ID, artworkId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/archives/artworks/{savedArtworkId}")
  @Override
  public ApiResponseBody<ArchiveWorkResponse> getArchiveWorkDetail(
      @PathVariable Long savedArtworkId, HttpServletRequest request) {
    ArchiveWorkResult result =
        getArchiveWorkDetailService.getArchiveWorkDetail(TEMP_USER_ID, savedArtworkId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/archives/artworks")
  @Override
  public ApiResponseBody<List<ArchiveWorkResponse>> getArchivedWorks(HttpServletRequest request) {
    List<ArchiveWorkResult> results = getArchivedWorksService.getArchivedWorks(TEMP_USER_ID);
    return ApiResponseBody.success(results.stream().map(mapper::toResponse).toList(), request);
  }
}
