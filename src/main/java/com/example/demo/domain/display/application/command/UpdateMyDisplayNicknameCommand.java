package com.example.demo.domain.display.application.command;

public record UpdateMyDisplayNicknameCommand(Long userId, Long displayId, String displayNickname) {}
