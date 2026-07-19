package com.example.demo.domain.memo.presentation;

import com.example.demo.domain.memo.application.command.DeleteArtworkMemoService;
import com.example.demo.domain.memo.application.command.DeleteExhibitionMemoService;
import com.example.demo.domain.memo.application.command.UpsertArtworkMemoCommand;
import com.example.demo.domain.memo.application.command.UpsertArtworkMemoService;
import com.example.demo.domain.memo.application.command.UpsertExhibitionMemoCommand;
import com.example.demo.domain.memo.application.command.UpsertExhibitionMemoService;
import com.example.demo.domain.memo.application.result.MemoResult;
import com.example.demo.domain.memo.presentation.docs.MemoControllerDocs;
import com.example.demo.domain.memo.presentation.mapper.MemoPresentationMapper;
import com.example.demo.domain.memo.presentation.request.MemoRequest;
import com.example.demo.domain.memo.presentation.response.MemoResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class MemoController implements MemoControllerDocs {

  // TODO: 인증 붙기 전까지 사용하는 임시 사용자 ID. 로그인 구현되면 인증 정보에서 꺼내오도록 교체해야 함.
  private static final Long TEMP_USER_ID = 1L;

  private final UpsertExhibitionMemoService upsertExhibitionMemoService;
  private final DeleteExhibitionMemoService deleteExhibitionMemoService;
  private final UpsertArtworkMemoService upsertArtworkMemoService;
  private final DeleteArtworkMemoService deleteArtworkMemoService;
  private final MemoPresentationMapper mapper;

  public MemoController(
      UpsertExhibitionMemoService upsertExhibitionMemoService,
      DeleteExhibitionMemoService deleteExhibitionMemoService,
      UpsertArtworkMemoService upsertArtworkMemoService,
      DeleteArtworkMemoService deleteArtworkMemoService,
      MemoPresentationMapper mapper) {
    this.upsertExhibitionMemoService = upsertExhibitionMemoService;
    this.deleteExhibitionMemoService = deleteExhibitionMemoService;
    this.upsertArtworkMemoService = upsertArtworkMemoService;
    this.deleteArtworkMemoService = deleteArtworkMemoService;
    this.mapper = mapper;
  }

  @PutMapping("/api/v1/archives/exhibitions/{archiveDisplayId}/memo")
  @Override
  public ApiResponseBody<MemoResponse> upsertExhibitionMemo(
      @PathVariable Long archiveDisplayId,
      @RequestBody MemoRequest request,
      HttpServletRequest httpRequest) {
    MemoResult result =
        upsertExhibitionMemoService.upsertExhibitionMemo(
            new UpsertExhibitionMemoCommand(
                TEMP_USER_ID, archiveDisplayId, request.content(), request.visitDate()));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @DeleteMapping("/api/v1/archives/exhibitions/{archiveDisplayId}/memo")
  @Override
  public ApiResponseBody<Void> deleteExhibitionMemo(
      @PathVariable Long archiveDisplayId, HttpServletRequest httpRequest) {
    deleteExhibitionMemoService.deleteExhibitionMemo(TEMP_USER_ID, archiveDisplayId);
    return ApiResponseBody.success(null, httpRequest);
  }

  @PutMapping("/api/v1/archives/artworks/{archiveWorkId}/memo")
  @Override
  public ApiResponseBody<MemoResponse> upsertArtworkMemo(
      @PathVariable Long archiveWorkId,
      @RequestBody MemoRequest request,
      HttpServletRequest httpRequest) {
    MemoResult result =
        upsertArtworkMemoService.upsertArtworkMemo(
            new UpsertArtworkMemoCommand(
                TEMP_USER_ID, archiveWorkId, request.content(), request.visitDate()));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @DeleteMapping("/api/v1/archives/artworks/{archiveWorkId}/memo")
  @Override
  public ApiResponseBody<Void> deleteArtworkMemo(
      @PathVariable Long archiveWorkId, HttpServletRequest httpRequest) {
    deleteArtworkMemoService.deleteArtworkMemo(TEMP_USER_ID, archiveWorkId);
    return ApiResponseBody.success(null, httpRequest);
  }
}
