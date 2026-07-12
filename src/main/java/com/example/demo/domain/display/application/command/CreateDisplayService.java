package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.application.result.CreateDisplayResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateDisplayService {

  private final DisplayRepository displayRepository;

  public CreateDisplayService(DisplayRepository displayRepository) {
    this.displayRepository = displayRepository;
  }

  @Transactional
  public CreateDisplayResult createDisplay(CreateDisplayCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display =
        Display.create(
            new UserId(command.ownerUserId()),
            command.title(),
            command.posterImageUrl(),
            command.subtitle(),
            command.content(),
            new DisplayLocation(command.placeName(), command.latitude(), command.longitude()),
            command.qnaAccount(),
            command.note(),
            command.organization(),
            command.department(),
            command.displayType(),
            command.displayFields(),
            new DisplayPeriod(
                command.startDate(), command.endDate(), command.startTime(), command.endTime()),
            command.artworkContentOpen(),
            command.exhibitionContentOpen());

    Display savedDisplay = displayRepository.save(display);
    return new CreateDisplayResult(savedDisplay.getId());
  }
}
