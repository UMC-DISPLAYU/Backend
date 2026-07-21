package com.example.demo.domain.user.application.command;

import com.example.demo.domain.user.domain.vo.Nickname;

public record ChangeNicknameCommand(Long userId, Nickname nickname) {}
