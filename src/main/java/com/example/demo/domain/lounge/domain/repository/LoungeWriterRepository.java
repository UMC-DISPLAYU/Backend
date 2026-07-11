package com.example.demo.domain.lounge.domain.repository;

import com.example.demo.domain.lounge.domain.vo.LoungeWriter;
import java.util.List;
import java.util.Map;

public interface LoungeWriterRepository {
  Map<Long, LoungeWriter> findByUserIds(List<Long> userIds);
}
