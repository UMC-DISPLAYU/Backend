package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record UpdateDisplayCommand(
    Long userId,
    Long displayId,
    String title,
    String posterImageUrl,
    DisplayType displayType,
    List<DisplayField> displayFields,
    String schoolOrOrganization,
    String departmentOrClub,
    String hostOrganizationName,
    String contract,
    String subtitle,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    LocalTime openTime,
    LocalTime closeTime,
    String placeName,
    String precautions) {}
