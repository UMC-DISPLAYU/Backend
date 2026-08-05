package com.example.demo.domain.displaycommunication.application.command;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply.ImageInfo;
import java.util.List;

public record CreateDisplayReviewReplyCommand(
    Long displayId, Long displayReviewId, Long userId, String content, List<ImageInfo> images) {}
