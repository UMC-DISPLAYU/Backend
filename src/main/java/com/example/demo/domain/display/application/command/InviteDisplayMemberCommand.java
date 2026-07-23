package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.domain.type.TeamMemberRole;

public record InviteDisplayMemberCommand(
    Long requesterUserId, Long displayId, Long inviteeUserId, TeamMemberRole role) {}
