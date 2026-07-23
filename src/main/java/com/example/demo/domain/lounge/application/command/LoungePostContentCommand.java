package com.example.demo.domain.lounge.application.command;

import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import java.util.List;

public record LoungePostContentCommand(
    String title, List<String> postImageUrls, String content, LoungePostCategory category) {}
