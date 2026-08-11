package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import java.util.List;

public record ArtworkFeelingReplyCommand(
    Long displayArtworkId,
    Long feelingId,
    Long userId,
    String content,
    List<ArtworkFeelingReply.ImageInfo> images) {}
