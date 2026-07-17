package com.example.demo.global.file.application.result;

public record PresignedUrlResult(
    String uploadUrl, String fileKey, String fileUrl, long expiresIn) {}
