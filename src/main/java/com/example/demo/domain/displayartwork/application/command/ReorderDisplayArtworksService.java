package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.displayartwork.application.result.ReorderDisplayArtworksResult;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.repository.DisplayArtworkRepository;
import com.example.demo.global.error.BusinessException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReorderDisplayArtworksService {

  private final DisplayRepository displayRepository;
  private final DisplayArtworkRepository displayArtworkRepository;

  public ReorderDisplayArtworksService(
      DisplayRepository displayRepository, DisplayArtworkRepository displayArtworkRepository) {
    this.displayRepository = displayRepository;
    this.displayArtworkRepository = displayArtworkRepository;
  }

  @Transactional
  public ReorderDisplayArtworksResult reorder(
      Long requesterUserId, ReorderDisplayArtworksCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    Display display =
        displayRepository
            .findById(command.displayId())
            .orElseThrow(() -> new BusinessException(DisplayArtworkErrorCode.DISPLAY_NOT_FOUND));

    if (!display.isTeamLeader(requesterUserId)) {
      throw new BusinessException(DisplayArtworkErrorCode.FORBIDDEN_ARTWORK_ACTION);
    }

    List<DisplayArtwork> artworks =
        displayArtworkRepository.findAllByDisplayId(command.displayId());
    Map<Long, DisplayArtwork> artworkById =
        artworks.stream().collect(Collectors.toMap(DisplayArtwork::getId, Function.identity()));

    List<Long> orderedArtworkIds = command.orderedArtworkIds();
    Set<Long> orderedIdSet = new HashSet<>(orderedArtworkIds);
    if (orderedIdSet.size() != orderedArtworkIds.size()
        || !orderedIdSet.equals(artworkById.keySet())) {
      throw new BusinessException(DisplayArtworkErrorCode.INVALID_ARTWORK_ORDER_LIST);
    }

    for (int i = 0; i < orderedArtworkIds.size(); i++) {
      artworkById.get(orderedArtworkIds.get(i)).changeWorkSortOrder(i);
    }

    return new ReorderDisplayArtworksResult(command.displayId(), orderedArtworkIds.size());
  }
}
