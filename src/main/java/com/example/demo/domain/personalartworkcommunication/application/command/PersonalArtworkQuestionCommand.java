package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion.ImageInfo;
import java.util.List;

public record PersonalArtworkQuestionCommand(
    Long personalArtworkId,
    Long userId,
    String content,
    boolean isPublic,
    List<ImageInfo> images) {}
