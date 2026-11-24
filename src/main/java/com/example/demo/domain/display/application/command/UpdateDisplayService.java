package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.application.permission.DisplayPermissionChecker;
import com.example.demo.domain.display.application.port.DisplayListCacheEvictionPort;
import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayScreening;
import com.example.demo.domain.display.domain.repository.DisplayLikeRepository;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.repository.DisplayScreeningRepository;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.file.application.MediaUsage;
import com.example.demo.global.file.application.event.MediaFilesRemovedEvent;
import com.example.demo.global.file.application.port.MediaStorageVerifier;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateDisplayService {

  private final DisplayRepository displayRepository;
  private final MediaStorageVerifier mediaStorageVerifier;
  private final ApplicationEventPublisher eventPublisher;
  private final DisplayScreeningRepository screeningRepository;
  private final DisplayLikeRepository displayLikeRepository;
  private final DisplayListCacheEvictionPort displayListCacheEvictionPort;
  private final DisplayPermissionChecker displayPermissionChecker;
  private final Clock clock;

  public UpdateDisplayService(
      DisplayRepository displayRepository,
      DisplayScreeningRepository screeningRepository,
      DisplayLikeRepository displayLikeRepository,
      DisplayListCacheEvictionPort displayListCacheEvictionPort,
      DisplayPermissionChecker displayPermissionChecker,
      MediaStorageVerifier mediaStorageVerifier,
      ApplicationEventPublisher eventPublisher,
      Clock clock) {
    this.displayRepository = displayRepository;
    this.screeningRepository = screeningRepository;
    this.displayLikeRepository = displayLikeRepository;
    this.displayListCacheEvictionPort = displayListCacheEvictionPort;
    this.displayPermissionChecker = displayPermissionChecker;
    this.mediaStorageVerifier = mediaStorageVerifier;
    this.eventPublisher = eventPublisher;
    this.clock = clock;
  }

  @Transactional
  public DisplayDetailResult updateDisplay(UpdateDisplayCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display =
        displayRepository
            .findById(command.displayId())
            .filter(candidate -> !candidate.isDeleted())
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
    displayPermissionChecker.requireTeamLeader(display, command.userId());

    if (command.posterMedia() != null) {
      mediaStorageVerifier.verifyVideo(command.posterMedia(), MediaUsage.DISPLAY);
    }
    if (command.displayMedia() != null) {
      command
          .displayMedia()
          .forEach(media -> mediaStorageVerifier.verifyVideo(media, MediaUsage.DISPLAY));
    }
    List<String> previousFileUrls = display.getMediaFileUrls();
    display.changeMedia(command.posterMedia(), command.displayMedia(), command.posterCrop());
    display.changePosterPresentation(command.posterFit(), command.posterEdgeColor());
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
    if (command.contract() != null) {
      display.changeContract(command.contract());
    }
    display.changePeriod(
        new DisplayPeriod(
            command.startDate() == null ? display.getPeriod().startDate() : command.startDate(),
            command.endDate() == null ? display.getPeriod().endDate() : command.endDate(),
            command.openTime() == null ? display.getPeriod().startTime() : command.openTime(),
            command.closeTime() == null ? display.getPeriod().endTime() : command.closeTime()));

    if (command.placeName() != null
        || command.latitude() != null
        || command.longitude() != null
        || command.roadAddress() != null) {
      display.changeLocation(
          new DisplayLocation(
              valueOrCurrent(command.placeName(), display.getLocation().placeName()),
              command.latitude() == null ? display.getLocation().latitude() : command.latitude(),
              command.longitude() == null ? display.getLocation().longitude() : command.longitude(),
              valueOrCurrent(command.roadAddress(), display.getLocation().roadAddress())));
    }

    if (command.region() != null) {
      display.changeRegion(command.region());
    }
    display.changeExternalLinks(
        updatedLink(command.instagramUrl(), display.getInstagramUrl()),
        updatedLink(command.websiteUrl(), display.getWebsiteUrl()));

    MediaFilesRemovedEvent removed =
        MediaFilesRemovedEvent.between(previousFileUrls, display.getMediaFileUrls());
    if (!removed.fileUrls().isEmpty()) {
      eventPublisher.publishEvent(removed);
    }
    displayListCacheEvictionPort.evictAfterCommit();
    return DisplayDetailResult.from(
        display,
        displayLikeRepository.countByDisplayId(display.getId()),
        display.getStatus() == DisplayStatus.PENDING_REVIEW
            ? screeningRepository
                .findLatestByDisplayId(display.getId())
                .map(DisplayScreening::getId)
                .orElse(null)
            : null,
        LocalDate.now(clock));
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

  private static String updatedLink(String value, String current) {
    return value == null ? current : value.isEmpty() ? null : value;
  }

  private static String valueOrCurrent(String value, String current) {
    return value == null ? current : value;
  }
}
