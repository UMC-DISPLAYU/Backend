package com.example.demo.domain.display.presentation.request;

import jakarta.validation.constraints.NotNull;

public record ChangeDisplayMemberRoleRequest(@NotNull DisplayMemberRoleRequest role) {}
