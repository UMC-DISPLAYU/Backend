package com.example.demo.domain.archive.application.query;

import com.example.demo.domain.archive.application.result.ArchiveWorkResult;
import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetArchivedWorksService {

  private final ArchiveWorkRepository archiveWorkRepository;

  public GetArchivedWorksService(ArchiveWorkRepository archiveWorkRepository) {
    this.archiveWorkRepository = archiveWorkRepository;
  }

  public List<ArchiveWorkResult> getArchivedWorks(Long userId) {
    return archiveWorkRepository.findAllByUserIdOrderBySavedAtDescIdDesc(userId).stream()
        .map(
            archiveWork ->
                new ArchiveWorkResult(
                    archiveWork.getId(),
                    archiveWork.getDisplayArtworkId(),
                    archiveWork.getUserId(),
                    archiveWork.getSavedAt()))
        .toList();
  }
}
