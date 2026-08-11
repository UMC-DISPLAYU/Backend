package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply.ImageInfo;
import java.util.List;

public record PersonalArtworkQuestionReplyCommand(
    Long personalArtworkId,
    Long personalQuestionId,
    Long userId,
    String content,
    List<ImageInfo> images) {}
