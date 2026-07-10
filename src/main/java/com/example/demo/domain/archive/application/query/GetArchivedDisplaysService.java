package com.example.demo.domain.archive.application.query;

import com.example.demo.domain.archive.application.result.ArchiveDisplayResult;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetArchivedDisplaysService {

  private final ArchiveDisplayRepository archiveDisplayRepository;

  public GetArchivedDisplaysService(ArchiveDisplayRepository archiveDisplayRepository) {
    this.archiveDisplayRepository = archiveDisplayRepository;
  }

  public List<ArchiveDisplayResult> getArchivedDisplays(Long userId) {
    return archiveDisplayRepository.findAllByUserIdOrderByIdDesc(userId).stream()
        .map(
            archiveDisplay ->
                new ArchiveDisplayResult(
                    archiveDisplay.getId(),
                    archiveDisplay.getDisplayId(),
                    archiveDisplay.getUserId()))
        .toList();
  }
}
