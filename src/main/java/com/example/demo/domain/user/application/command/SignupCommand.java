package com.example.demo.domain.user.application.command;

import com.example.demo.domain.user.domain.vo.Nickname;

import java.util.List;

public record SignupCommand(Nickname nickname, List<AgreementCommand> agreements) {}
