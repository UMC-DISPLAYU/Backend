package com.example.demo.domain.display.presentation.response;

public record DisplayLeaderTransferResponse(
    Long displayId, MemberResponse previousLeader, MemberResponse newLeader) {

  public record MemberResponse(Long teamMemberId, Long userId, String role) {}
}
