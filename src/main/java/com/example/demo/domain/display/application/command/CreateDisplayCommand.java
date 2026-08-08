package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateDisplayCommand(
    Long ownerUserId,
    String title,
    String posterImageUrl,
    List<String> displayImageUrls,
    String subtitle,
    String content,
    String placeName,
    BigDecimal latitude,
    BigDecimal longitude,
    String roadAddress,
    String qnaAccount,
    String note,
    String organization,
    String department,
    String displayNickname,
    String contract,
    DisplayType displayType,
    List<DisplayField> displayFields,
    DisplayRegion region,
    LocalDate startDate,
    LocalDate endDate,
    LocalTime startTime,
    LocalTime endTime,
    ContentOpenPolicy artworkContentOpen,
    ContentOpenPolicy exhibitionContentOpen) {}
