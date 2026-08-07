package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply.ImageInfo;
import java.util.List;

public record PersonalArtworkFeelingReplyCommand(
    Long personalArtworkId,
    Long personalFeelingId,
    Long userId,
    String content,
    List<ImageInfo> images) {}
