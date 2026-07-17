package com.example.demo.global.file.presentation.mapper;

import com.example.demo.global.file.application.command.CreatePresignedUrlCommand;
import com.example.demo.global.file.application.result.PresignedUrlResult;
import com.example.demo.global.file.presentation.request.CreatePresignedUrlRequest;
import com.example.demo.global.file.presentation.response.PresignedUrlResponse;
import org.springframework.stereotype.Component;

@Component
public class FilePresentationMapper {

  public CreatePresignedUrlCommand toCommand(CreatePresignedUrlRequest request) {
    return new CreatePresignedUrlCommand(
        request.fileType(),
        request.domain(),
        request.fileName(),
        request.contentType(),
        request.fileSize());
  }

  public PresignedUrlResponse toResponse(PresignedUrlResult result) {
    return new PresignedUrlResponse(
        result.uploadUrl(), result.fileKey(), result.fileUrl(), result.expiresIn());
  }
}
