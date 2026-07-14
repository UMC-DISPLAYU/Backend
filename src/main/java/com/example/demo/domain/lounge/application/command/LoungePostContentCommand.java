package com.example.demo.domain.lounge.application.command;

import com.example.demo.domain.lounge.domain.type.LoungePostCategory;

public record LoungePostContentCommand(
    String title, String postImageUrl, String content, LoungePostCategory category) {}
