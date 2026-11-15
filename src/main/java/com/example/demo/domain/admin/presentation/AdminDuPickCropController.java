package com.example.demo.domain.admin.presentation;

import com.example.demo.domain.admin.application.service.UpdateDuPickCropService;
import com.example.demo.domain.admin.presentation.mapper.DuPickCropMapper;
import com.example.demo.domain.admin.presentation.request.UpdateDuPickCropRequest;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/du-picks")
@SecurityRequirement(name = "Authorization")
public class AdminDuPickCropController {
  private final UpdateDuPickCropService service;
  private final DuPickCropMapper mapper;

  @PatchMapping("/{duPickId}/poster-crop")
  @Operation(
      summary = "관리자 디유픽 칼럼 배너 크롭 수정",
      description = "크롭은 이미지 전체 대비 비율이며 null 또는 생략 시 해제합니다.")
  public ApiResponseBody<Void> update(
      @AuthenticationPrincipal AuthUser operator,
      @PathVariable Long duPickId,
      @Valid @RequestBody UpdateDuPickCropRequest body,
      HttpServletRequest request) {
    service.update(operator == null ? null : operator.userId(), duPickId, mapper.toCrop(body));
    return ApiResponseBody.success(null, request);
  }
}
