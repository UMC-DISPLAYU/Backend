package com.example.demo.domain.displaycommunication.application.command;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview.ImageInfo;
import java.util.List;

public record CreateDisplayReviewCommand(
    Long displayId, Long userId, String content, List<ImageInfo> images) {}
