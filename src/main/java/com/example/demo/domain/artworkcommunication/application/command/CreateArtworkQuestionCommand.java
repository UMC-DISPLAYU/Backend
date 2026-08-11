package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion.ImageInfo;
import java.util.List;

public record CreateArtworkQuestionCommand(
    Long displayArtworkId, Long userId, String content, Boolean isPublic, List<ImageInfo> images) {}
