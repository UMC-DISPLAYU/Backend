package com.example.demo.domain.memo.presentation.mapper;

import com.example.demo.domain.memo.application.result.MemoResult;
import com.example.demo.domain.memo.presentation.response.MemoResponse;
import org.springframework.stereotype.Component;

@Component
public class MemoPresentationMapper {

  public MemoResponse toResponse(MemoResult result) {
    return new MemoResponse(
        result.memoId(),
        result.archiveDisplayId(),
        result.archiveWorkId(),
        result.content(),
        result.visitDate(),
        result.createdAt(),
        result.updatedAt());
  }
}
