package com.example.demo.domain.admin.presentation;

import com.example.demo.domain.admin.application.query.ContentSearchQuery;
import com.example.demo.domain.admin.application.service.AdminContentService;
import com.example.demo.domain.admin.presentation.mapper.AdminContentMapper;
import com.example.demo.domain.admin.presentation.response.ContentResponses;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/contents")
@ConditionalOnProperty(name = "admin.contents.enabled", havingValue = "true")
@SecurityRequirement(name = "Authorization")
public class AdminContentController {
  private final AdminContentService service;
  private final AdminContentMapper mapper;

  @GetMapping
  @Operation(summary = "관리자 콘텐츠 목록 조회")
  public ApiResponseBody<ContentResponses.Page> search(
      @AuthenticationPrincipal AuthUser admin,
      @RequestParam(required = false) String q,
      @Parameter(description = "DISPLAY / ARTWORK. 생략 시 두 종류 모두 조회, 개인 작품 제외")
          @RequestParam(required = false)
          String contentType,
      @RequestParam(required = false) String operationStatus,
      @Parameter(
              description = "이전 nextCursor를 그대로 전달하는 문자열 토큰(최대 1024자). 첫 페이지는 생략하고 검색·필터 변경 시 초기화")
          @RequestParam(required = false)
          String cursor,
      @RequestParam(defaultValue = "20") int size,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            service.search(
                adminId(admin),
                new ContentSearchQuery(q, contentType, operationStatus, cursor, size))),
        request);
  }

  @GetMapping("/{contentType}/{contentId}")
  @Operation(summary = "관리자 콘텐츠 상세 조회")
  public ApiResponseBody<ContentResponses.Detail> detail(
      @AuthenticationPrincipal AuthUser admin,
      @PathVariable String contentType,
      @PathVariable Long contentId,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(service.getDetail(adminId(admin), contentType, contentId)), request);
  }

  private Long adminId(AuthUser admin) {
    return admin == null ? null : admin.userId();
  }
}
