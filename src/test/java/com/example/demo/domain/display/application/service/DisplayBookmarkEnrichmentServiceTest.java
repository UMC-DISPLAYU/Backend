package com.example.demo.domain.display.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.domain.display.application.result.SearchDisplayResult;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DisplayBookmarkEnrichmentServiceTest {

  @Test
  void enrichSearchDisplayResultUsesBulkArchiveLookup() {
    FakeArchiveDisplayRepository repository = new FakeArchiveDisplayRepository(List.of(2L));
    DisplayBookmarkEnrichmentService service = new DisplayBookmarkEnrichmentService(repository);
    SearchDisplayResult result =
        new SearchDisplayResult(
            List.of(exhibition(1L), exhibition(2L), exhibition(3L)),
            new SearchDisplayResult.PaginationResult(null, 3, false));

    SearchDisplayResult enriched = service.enrich(result, 10L);

    assertThat(repository.bulkLookupCount).isEqualTo(1);
    assertThat(repository.requestedUserId).isEqualTo(10L);
    assertThat(repository.requestedDisplayIds).containsExactly(1L, 2L, 3L);
    assertThat(enriched.exhibitions())
        .extracting(SearchDisplayResult.ExhibitionResult::isArchived)
        .containsExactly(false, true, false);
  }

  private static SearchDisplayResult.ExhibitionResult exhibition(Long displayId) {
    return new SearchDisplayResult.ExhibitionResult(
        displayId,
        "전시 " + displayId,
        "https://cdn.displayu.com/posters/main.png",
        LocalDate.of(2026, 7, 1),
        LocalDate.of(2026, 7, 10),
        9,
        false);
  }

  private static class FakeArchiveDisplayRepository implements ArchiveDisplayRepository {

    private final List<Long> archivedDisplayIds;
    private Long requestedUserId;
    private List<Long> requestedDisplayIds;
    private int bulkLookupCount;

    private FakeArchiveDisplayRepository(List<Long> archivedDisplayIds) {
      this.archivedDisplayIds = archivedDisplayIds;
    }

    @Override
    public Optional<ArchiveDisplay> findByIdAndUserId(Long archiveDisplayId, Long userId) {
      return Optional.empty();
    }

    @Override
    public Optional<ArchiveDisplay> findByUserIdAndDisplayId(Long userId, Long displayId) {
      return Optional.empty();
    }

    @Override
    public List<Long> findDisplayIdsByUserIdAndDisplayIdIn(Long userId, List<Long> displayIds) {
      requestedUserId = userId;
      requestedDisplayIds = displayIds;
      bulkLookupCount++;
      return archivedDisplayIds;
    }

    @Override
    public List<ArchiveDisplay> findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
        Long userId, Long cursorId, int limit) {
      return List.of();
    }

    @Override
    public ArchiveDisplay save(ArchiveDisplay archiveDisplay) {
      return archiveDisplay;
    }

    @Override
    public void delete(ArchiveDisplay archiveDisplay) {}
  }
}
