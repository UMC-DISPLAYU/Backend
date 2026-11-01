package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.domain.type.TeamMemberRole;

public record ChangeDisplayMemberRoleCommand(
    Long requesterUserId, Long displayId, Long teamMemberId, TeamMemberRole role) {}
