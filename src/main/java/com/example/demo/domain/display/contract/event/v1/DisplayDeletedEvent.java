package com.example.demo.domain.display.contract.event.v1;

import java.time.LocalDateTime;
import java.util.UUID;

public record DisplayDeletedEvent(UUID eventId, Long displayId, LocalDateTime deletedAt) {}
