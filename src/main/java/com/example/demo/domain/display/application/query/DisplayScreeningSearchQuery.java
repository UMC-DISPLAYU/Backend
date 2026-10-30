package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.domain.type.DisplayStatus;

/** keyword는 전시명·학교·학과 부분 일치 또는 숫자면 전시 ID 일치. null이면 검색어 조건 없음. */
public record DisplayScreeningSearchQuery(
    DisplayStatus status, String keyword, Long cursor, int size) {}
