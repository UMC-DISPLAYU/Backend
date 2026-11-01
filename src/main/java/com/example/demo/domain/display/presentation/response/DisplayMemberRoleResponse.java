package com.example.demo.domain.display.presentation.response;

public record DisplayMemberRoleResponse(
    Long displayId, Long teamMemberId, Long userId, String role) {}
