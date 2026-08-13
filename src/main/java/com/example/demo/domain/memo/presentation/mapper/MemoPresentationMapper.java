package com.example.demo.domain.memo.presentation.mapper;

import com.example.demo.domain.memo.application.command.UpsertArtworkMemoCommand;
import com.example.demo.domain.memo.application.command.UpsertExhibitionMemoCommand;
import com.example.demo.domain.memo.application.command.UpsertPersonalWorkMemoCommand;
import com.example.demo.domain.memo.application.result.MemoResult;
import com.example.demo.domain.memo.presentation.request.MemoRequest;
import com.example.demo.domain.memo.presentation.response.MemoResponse;
import org.springframework.stereotype.Component;

@Component
public class MemoPresentationMapper {

  public UpsertExhibitionMemoCommand toExhibitionMemoCommand(
      Long archiveDisplayId, Long userId, MemoRequest request) {
    return new UpsertExhibitionMemoCommand(
        userId, archiveDisplayId, request.content(), request.visitDate());
  }

  public UpsertArtworkMemoCommand toArtworkMemoCommand(
      Long archiveWorkId, Long userId, MemoRequest request) {
    return new UpsertArtworkMemoCommand(
        userId, archiveWorkId, request.content(), request.visitDate());
  }

  public UpsertPersonalWorkMemoCommand toPersonalWorkMemoCommand(
      Long archivePersonalWorkId, Long userId, MemoRequest request) {
    return new UpsertPersonalWorkMemoCommand(
        userId, archivePersonalWorkId, request.content(), request.visitDate());
  }

  public MemoResponse toResponse(MemoResult result) {
    return new MemoResponse(
        result.memoId(),
        result.archiveDisplayId(),
        result.archiveWorkId(),
        result.archivePersonalWorkId(),
        result.content(),
        result.visitDate(),
        result.createdAt(),
        result.updatedAt());
  }
}
