package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateDisplayService {

  private final DisplayRepository displayRepository;

  public UpdateDisplayService(DisplayRepository displayRepository) {
    this.displayRepository = displayRepository;
  }

  @Transactional
  public DisplayDetailResult updateDisplay(UpdateDisplayCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display =
        displayRepository
            .findById(command.displayId())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
    if (!display.isTeamLeader(command.userId())) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }

    if (command.posterImageUrl() != null) {
      display.changePosterImageUrl(command.posterImageUrl());
    }
    if (command.displayFields() != null) {
      display.changeDisplayFields(command.displayFields());
    }

    DisplayType displayType =
        command.displayType() == null ? display.getDisplayType() : command.displayType();
    display.changeClassification(displayType);
    display.changeBasicInfo(
        valueOrCurrent(command.title(), display.getTitle()),
        valueOrCurrent(command.subtitle(), display.getSubtitle()),
        valueOrCurrent(command.description(), display.getContent()),
        display.getQnaAccount(),
        valueOrCurrent(command.precautions(), display.getNote()),
        organization(command, displayType, display.getOrganization()),
        department(command, displayType, display.getDepartment()));
    display.changePeriod(
        new DisplayPeriod(
            command.startDate() == null ? display.getPeriod().startDate() : command.startDate(),
            command.endDate() == null ? display.getPeriod().endDate() : command.endDate(),
            command.openTime() == null ? display.getPeriod().startTime() : command.openTime(),
            command.closeTime() == null ? display.getPeriod().endTime() : command.closeTime()));

    if (command.placeName() != null) {
      display.changeLocation(
          new DisplayLocation(
              command.placeName(),
              display.getLocation().latitude(),
              display.getLocation().longitude()));
    }

    return DisplayDetailResult.from(display);
  }

  private String organization(
      UpdateDisplayCommand command, DisplayType displayType, String currentOrganization) {
    if (displayType == DisplayType.GRADUATION || displayType == DisplayType.ASSIGNMENTS) {
      return valueOrCurrent(command.schoolOrOrganization(), currentOrganization);
    }
    return valueOrCurrent(command.hostOrganizationName(), currentOrganization);
  }

  private String department(
      UpdateDisplayCommand command, DisplayType displayType, String currentDepartment) {
    if (displayType == DisplayType.GRADUATION || displayType == DisplayType.ASSIGNMENTS) {
      return valueOrCurrent(command.departmentOrClub(), currentDepartment);
    }
    return "";
  }

  private static String valueOrCurrent(String value, String current) {
    return value == null ? current : value;
  }
}
