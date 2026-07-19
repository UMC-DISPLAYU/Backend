package com.example.demo.domain.user.application.result;

import java.time.LocalDateTime;

public record ChangeNicknameResult(String nickname, LocalDateTime nextNicknameChangeAvailableAt) {}
