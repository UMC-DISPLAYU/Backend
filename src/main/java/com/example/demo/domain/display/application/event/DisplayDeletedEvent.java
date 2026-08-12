package com.example.demo.domain.display.application.event;

import java.time.LocalDateTime;

public record DisplayDeletedEvent(Long displayId, LocalDateTime deletedAt) {}
