package com.example.demo.domain.display.presentation.response;

import java.time.LocalDateTime;

public record DisplayInvitationDisableResponse(
    Long displayId, LocalDateTime invitationDisabledAt) {}
