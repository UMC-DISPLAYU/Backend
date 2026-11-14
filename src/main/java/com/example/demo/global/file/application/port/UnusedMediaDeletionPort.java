package com.example.demo.global.file.application.port;

import java.util.List;

public interface UnusedMediaDeletionPort {
  void deleteAll(List<String> fileUrls);
}
