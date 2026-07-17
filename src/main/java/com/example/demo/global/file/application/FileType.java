package com.example.demo.global.file.application;

public enum FileType {
  IMAGE("images"),
  VIDEO("videos");

  private final String directoryName;

  FileType(String directoryName) {
    this.directoryName = directoryName;
  }

  public String directoryName() {
    return directoryName;
  }
}
