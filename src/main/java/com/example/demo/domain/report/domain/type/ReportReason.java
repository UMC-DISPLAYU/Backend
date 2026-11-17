package com.example.demo.domain.report.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportReason {
  SPAM_ADVERTISEMENT("스팸·광고"),
  ABUSE_DEFAMATION("욕설·비방"),
  OBSCENE_OFFENSIVE_CONTENT("음란·불쾌한 내용"),
  PERSONAL_INFORMATION_EXPOSURE("개인정보 노출"),
  OTHER("기타");
  private final String description;
}
