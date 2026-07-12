package com.example.demo.domain.user.presentation.request;



import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(

        @NotBlank
        String refreshToken

) {
}