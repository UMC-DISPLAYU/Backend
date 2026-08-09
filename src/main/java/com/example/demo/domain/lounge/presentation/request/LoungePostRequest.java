package com.example.demo.domain.lounge.presentation.request;

import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record LoungePostRequest(
    @NotBlank @Size(max = 100) String title,
    @NotNull @Size(max = 5) List<@NotBlank @Size(max = 2048) String> postImageUrls,
    @NotBlank @Size(max = 1500) String content,
    @NotNull LoungePostCategory category) {}
