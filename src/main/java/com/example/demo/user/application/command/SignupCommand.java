package com.example.demo.user.application.command;

import java.util.List;

public record SignupCommand(String nickname, List<AgreementCommand> agreements) {}
