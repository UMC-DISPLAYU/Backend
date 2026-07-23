package com.example.demo.domain.display.application.command;

public record RejectDisplayInvitationCommand(Long requesterUserId, Long invitationId) {}
