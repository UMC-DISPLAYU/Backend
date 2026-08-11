package com.example.demo.domain.user.application.command;

import com.example.demo.domain.user.domain.type.AgreementCode;

public record AgreementCommand(AgreementCode code, String version) {}
