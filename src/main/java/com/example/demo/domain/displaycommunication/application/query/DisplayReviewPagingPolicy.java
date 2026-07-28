package com.example.demo.domain.displaycommunication.application.query;

import org.springframework.stereotype.Component;

@Component
public class DisplayReviewPagingPolicy {

  private static final int MAX_PAGE_SIZE = 50;

  public int normalize(int requestedSize) {
    return Math.min(Math.max(requestedSize, 1), MAX_PAGE_SIZE);
  }
}
