package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling.ImageInfo;
import java.util.List;

public record PersonalArtworkFeelingCommand(
    Long personalArtworkId, Long userId, String content, List<ImageInfo> images) {}
