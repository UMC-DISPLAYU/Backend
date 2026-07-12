package com.example.demo.domain.archive.presentation.docs;

import com.example.demo.domain.archive.presentation.response.ArchiveWorkResponse;
import com.example.demo.domain.archive.presentation.response.ArchiveWorkToggleResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Archive", description = "개인 아카이브 API")
public interface ArchiveWorkControllerDocs {

  @Operation(summary = "작품 저장", description = "작품을 개인 아카이브에 저장합니다.")
  ApiResponseBody<ArchiveWorkToggleResponse> saveArchiveWork(
      @Parameter(description = "작품(DisplayArtwork) ID", example = "1") @PathVariable Long artworkId,
      HttpServletRequest request);

  @Operation(summary = "작품 저장 취소", description = "개인 아카이브에서 작품 저장을 취소합니다.")
  ApiResponseBody<ArchiveWorkToggleResponse> deleteArchiveWork(
      @Parameter(description = "작품(DisplayArtwork) ID", example = "1") @PathVariable Long artworkId,
      HttpServletRequest request);

  @Operation(summary = "저장된 작품 상세 조회", description = "저장 기록 ID로 저장된 작품 상세를 조회합니다.")
  ApiResponseBody<ArchiveWorkResponse> getArchiveWorkDetail(
      @Parameter(description = "저장된 작품(아카이브 기록) ID", example = "1") @PathVariable
          Long savedArtworkId,
      HttpServletRequest request);

  @Operation(summary = "저장된 작품 목록 조회", description = "내가 저장한 작품 목록을 최근 저장한 순으로 조회합니다.")
  ApiResponseBody<List<ArchiveWorkResponse>> getArchivedWorks(HttpServletRequest request);
}
