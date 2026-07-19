package com.example.demo.domain.displayartwork.application.command;

import java.util.List;

public record ReorderDisplayArtworksCommand(Long displayId, List<Long> orderedArtworkIds) {}
