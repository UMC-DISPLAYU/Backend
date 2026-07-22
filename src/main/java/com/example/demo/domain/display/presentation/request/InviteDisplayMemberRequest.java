package com.example.demo.domain.display.presentation.request;

import com.example.demo.domain.display.domain.type.TeamMemberRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InviteDisplayMemberRequest(
    @NotNull @Positive Long inviteeUserId, TeamMemberRole role) {}
