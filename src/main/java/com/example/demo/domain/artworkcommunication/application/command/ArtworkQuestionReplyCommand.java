package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply.ImageInfo;
import java.util.List;

public record ArtworkQuestionReplyCommand(
    Long displayArtworkId, Long questionId, Long userId, String content, List<ImageInfo> images) {}
