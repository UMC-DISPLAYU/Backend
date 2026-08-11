package com.example.demo.domain.archive.application.query;

import com.example.demo.domain.archive.application.result.ArchiveArtistCursorResult;
import com.example.demo.domain.archive.application.result.ArchiveArtistResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import com.example.demo.domain.archive.domain.repository.ArchiveArtistRepository;
import com.example.demo.domain.artist.application.result.ArtistProfileSummaryResult;
import com.example.demo.domain.artist.application.usecase.GetArtistProfileSummariesUseCase;
import com.example.demo.domain.displayartwork.application.result.ArtistWorkStatsResult;
import com.example.demo.domain.displayartwork.application.usecase.GetArtistWorkStatsUseCase;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetArchivedArtistsService {

  private static final int MAX_PAGE_SIZE = 50;

  private final ArchiveArtistRepository archiveArtistRepository;
  private final GetArtistProfileSummariesUseCase getArtistProfileSummariesUseCase;
  private final GetArtistWorkStatsUseCase getArtistWorkStatsUseCase;

  public GetArchivedArtistsService(
      ArchiveArtistRepository archiveArtistRepository,
      GetArtistProfileSummariesUseCase getArtistProfileSummariesUseCase,
      GetArtistWorkStatsUseCase getArtistWorkStatsUseCase) {
    this.archiveArtistRepository = archiveArtistRepository;
    this.getArtistProfileSummariesUseCase = getArtistProfileSummariesUseCase;
    this.getArtistWorkStatsUseCase = getArtistWorkStatsUseCase;
  }

  @Transactional(readOnly = true)
  public ArchiveArtistCursorResult getArchivedArtists(Long userId, Long cursorId, int size) {
    int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    List<ArchiveArtist> fetched =
        archiveArtistRepository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            userId, cursorId, pageSize + 1);

    boolean hasNext = fetched.size() > pageSize;
    List<ArchiveArtist> artists = hasNext ? fetched.subList(0, pageSize) : fetched;

    Map<Long, ArtistProfileSummaryResult> profileByArtistProfileId =
        artists.isEmpty()
            ? Map.of()
            : getArtistProfileSummariesUseCase
                .getArtistProfileSummaries(
                    artists.stream().map(ArchiveArtist::getArtistProfileId).toList())
                .stream()
                .collect(
                    Collectors.toMap(
                        ArtistProfileSummaryResult::artistProfileId, profile -> profile));

    List<Long> artistUserIds =
        profileByArtistProfileId.values().stream().map(ArtistProfileSummaryResult::userId).toList();
    Map<Long, ArtistWorkStatsResult> statsByArtistUserId =
        artistUserIds.isEmpty()
            ? Map.of()
            : getArtistWorkStatsUseCase.getArtistWorkStats(artistUserIds).stream()
                .collect(Collectors.toMap(ArtistWorkStatsResult::userId, stats -> stats));

    List<ArchiveArtistResult> results =
        artists.stream()
            .map(
                archiveArtist -> {
                  ArtistProfileSummaryResult profile =
                      profileByArtistProfileId.get(archiveArtist.getArtistProfileId());
                  ArtistWorkStatsResult stats =
                      profile == null ? null : statsByArtistUserId.get(profile.userId());
                  return ArchiveArtistResult.from(archiveArtist, profile, stats);
                })
            .toList();

    Long nextCursorId = hasNext ? results.get(results.size() - 1).archiveArtistId() : null;
    return new ArchiveArtistCursorResult(results, nextCursorId, pageSize, hasNext);
  }
}
