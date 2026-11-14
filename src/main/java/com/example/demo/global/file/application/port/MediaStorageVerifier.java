package com.example.demo.global.file.application.port;

import com.example.demo.global.file.application.MediaUsage;
import com.example.demo.global.file.domain.MediaContent;

public interface MediaStorageVerifier {
  void verifyVideo(MediaContent media, MediaUsage usage);
}
