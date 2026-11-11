package com.example.demo.domain.display.application.command;

public record TransferDisplayLeaderCommand(
    Long requesterUserId, Long displayId, Long targetMemberId) {}
