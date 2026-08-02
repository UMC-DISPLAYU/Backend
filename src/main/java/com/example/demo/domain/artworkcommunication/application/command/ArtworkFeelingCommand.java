package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling.ImageInfo;
import java.util.List;

public record ArtworkFeelingCommand(
    Long displayArtworkId, Long userId, String content, List<ImageInfo> images) {}
