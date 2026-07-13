package com.example.demo.domain.user.presentation;


import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import com.example.demo.domain.user.application.service.SendSchoolEmailVerificationService;
import com.example.demo.domain.user.presentation.docs.SchoolEmailVerificationControllerDocs;
import com.example.demo.domain.user.presentation.request.SchoolEmailVerificationRequest;
import com.example.demo.global.response.ApiResponseBody;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me/verification/email")
public class SchoolEmailVerificationController
        implements SchoolEmailVerificationControllerDocs {


    private final SendSchoolEmailVerificationService service;


    @Override
    @PostMapping("/send")
    public ResponseEntity<ApiResponseBody<Void>> send(
            @RequestBody SchoolEmailVerificationRequest request,
            HttpServletRequest httpRequest
    ) throws IOException {


        service.execute(
                new SendSchoolEmailVerificationCommand(
                        request.schoolEmail(),
                        request.univName()
                )
        );


        return ResponseEntity.ok(
                ApiResponseBody.success(null, httpRequest)
        );
    }
}