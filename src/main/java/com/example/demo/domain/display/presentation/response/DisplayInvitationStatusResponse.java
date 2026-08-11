package com.example.demo.domain.display.presentation.response;

import java.time.LocalDateTime;

public record DisplayInvitationStatusResponse(
    Long displayId, boolean enabled, String invitationUrl, LocalDateTime invitationDisabledAt) {}
