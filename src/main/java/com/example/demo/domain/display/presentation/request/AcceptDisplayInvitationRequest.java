package com.example.demo.domain.display.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AcceptDisplayInvitationRequest(@NotBlank @Size(max = 255) String displayNickname) {}
