package com.example.demo.domain.memo.presentation;

import com.example.demo.domain.memo.application.command.DeleteArtworkMemoService;
import com.example.demo.domain.memo.application.command.DeleteExhibitionMemoService;
import com.example.demo.domain.memo.application.command.UpsertArtworkMemoService;
import com.example.demo.domain.memo.application.command.UpsertExhibitionMemoService;
import com.example.demo.domain.memo.application.result.MemoResult;
import com.example.demo.domain.memo.presentation.docs.MemoControllerDocs;
import com.example.demo.domain.memo.presentation.mapper.MemoPresentationMapper;
import com.example.demo.domain.memo.presentation.request.MemoRequest;
import com.example.demo.domain.memo.presentation.response.MemoResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class MemoController implements MemoControllerDocs {

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
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    MemoResult result =
        upsertExhibitionMemoService.upsertExhibitionMemo(
            mapper.toExhibitionMemoCommand(archiveDisplayId, requireUserId(user), request));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @DeleteMapping("/api/v1/archives/exhibitions/{archiveDisplayId}/memo")
  @Override
  public ApiResponseBody<Void> deleteExhibitionMemo(
      @PathVariable Long archiveDisplayId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    deleteExhibitionMemoService.deleteExhibitionMemo(requireUserId(user), archiveDisplayId);
    return ApiResponseBody.success(null, httpRequest);
  }

  @PutMapping("/api/v1/archives/artworks/{archiveWorkId}/memo")
  @Override
  public ApiResponseBody<MemoResponse> upsertArtworkMemo(
      @PathVariable Long archiveWorkId,
      @RequestBody MemoRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    MemoResult result =
        upsertArtworkMemoService.upsertArtworkMemo(
            mapper.toArtworkMemoCommand(archiveWorkId, requireUserId(user), request));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @DeleteMapping("/api/v1/archives/artworks/{archiveWorkId}/memo")
  @Override
  public ApiResponseBody<Void> deleteArtworkMemo(
      @PathVariable Long archiveWorkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    deleteArtworkMemoService.deleteArtworkMemo(requireUserId(user), archiveWorkId);
    return ApiResponseBody.success(null, httpRequest);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
