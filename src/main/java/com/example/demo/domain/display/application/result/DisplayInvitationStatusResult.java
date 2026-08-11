package com.example.demo.domain.display.application.result;

import java.time.LocalDateTime;

public record DisplayInvitationStatusResult(
    Long displayId, boolean enabled, String invitationUrl, LocalDateTime invitationDisabledAt) {}
