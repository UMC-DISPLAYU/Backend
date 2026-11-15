package com.example.demo.domain.display.application.command;

public record CancelDisplayInvitationCommand(Long requesterUserId, Long invitationId) {}
