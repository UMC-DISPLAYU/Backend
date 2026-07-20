package com.example.demo.domain.user.presentation.response;

import java.time.LocalDateTime;

public record ChangeNicknameResponse(
    String nickname, LocalDateTime nextNicknameChangeAvailableAt) {}
