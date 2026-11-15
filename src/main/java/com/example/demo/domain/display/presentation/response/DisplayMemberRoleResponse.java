package com.example.demo.domain.display.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record DisplayMemberRoleResponse(
    @Schema(example = "1") Long displayId,
    @Schema(example = "11") Long teamMemberId,
    @Schema(example = "102") Long userId,
    @Schema(example = "MANAGER") String role) {}
