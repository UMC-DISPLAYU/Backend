package com.example.demo.domain.user.application.command;

import com.example.demo.domain.user.domain.vo.Nickname;
import com.example.demo.domain.user.domain.vo.ProfileImageUrl;

public record UpdateMyProfileCommand(
    Long userId, ProfileImageUrl profileImageUrl, Nickname nickname) {}
