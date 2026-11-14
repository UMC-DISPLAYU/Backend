package com.example.demo.global.file.application.port;

import java.util.Set;

public interface MediaReferenceQuery {
  Set<String> findReferencedUrls(Set<String> candidates);
}
