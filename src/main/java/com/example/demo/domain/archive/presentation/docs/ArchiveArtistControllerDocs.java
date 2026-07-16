package com.example.demo.domain.archive.presentation.docs;

import com.example.demo.domain.archive.presentation.response.ArchiveArtistCursorResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveArtistResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveArtistToggleResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Archive", description = "개인 아카이브 API")
public interface ArchiveArtistControllerDocs {

  @Operation(summary = "작가 저장", description = "작가를 개인 아카이브에 저장합니다.")
  ApiResponseBody<ArchiveArtistToggleResponse> saveArchiveArtist(
      @Parameter(description = "작가(Creator) ID", example = "1") @PathVariable @Positive Long artistId,
      HttpServletRequest request);

  @Operation(summary = "작가 저장 취소", description = "개인 아카이브에서 작가 저장을 취소합니다.")
  ApiResponseBody<ArchiveArtistToggleResponse> deleteArchiveArtist(
      @Parameter(description = "작가(Creator) ID", example = "1") @PathVariable @Positive Long artistId,
      HttpServletRequest request);

  @Operation(summary = "저장된 작가 상세 조회", description = "저장 기록 ID로 저장된 작가 상세를 조회합니다.")
  ApiResponseBody<ArchiveArtistResponse> getArchiveArtistDetail(
      @Parameter(description = "저장된 작가(아카이브 기록) ID", example = "1") @PathVariable @Positive Long savedArtistId,
      HttpServletRequest request);

  @Operation(summary = "저장된 작가 목록 조회", description = "내가 저장한 작가 목록을 최근 저장한 순으로 커서 기반 페이지네이션 조회합니다.")
  ApiResponseBody<ArchiveArtistCursorResponse> getArchivedArtists(
      @Parameter(description = "마지막으로 조회한 저장 기록 ID. 첫 요청이면 전달하지 않음")
          @RequestParam(required = false)
          @Positive Long cursorId,
      @Parameter(description = "한 번에 불러올 개수") @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
      HttpServletRequest request);
}
