package com.example.demo.domain.display.presentation.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InviteDisplayMemberRequest(
    @NotNull @Positive Long inviteeUserId, InviteDisplayMemberRoleRequest role) {}
