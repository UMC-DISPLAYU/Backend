package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.aggregate.Display;

public record DisplayLeaderTransferResult(
    Long displayId, DisplayMemberRoleResult previousLeader, DisplayMemberRoleResult newLeader) {

  public static DisplayLeaderTransferResult from(Long displayId, Display.LeaderTransfer transfer) {
    return new DisplayLeaderTransferResult(
        displayId,
        DisplayMemberRoleResult.from(displayId, transfer.previousLeader()),
        DisplayMemberRoleResult.from(displayId, transfer.newLeader()));
  }
}
