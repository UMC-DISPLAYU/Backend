package com.example.demo.global.file.application.command;

import com.example.demo.global.file.application.FileType;

public record CreatePresignedUrlCommand(
    FileType fileType, String domain, String fileName, String contentType, long fileSize) {}
