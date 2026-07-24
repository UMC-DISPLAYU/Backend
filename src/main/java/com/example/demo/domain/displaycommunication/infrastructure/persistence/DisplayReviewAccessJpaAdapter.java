package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewAccessRepository;
import com.example.demo.domain.displaycommunication.domain.repository.DisplayReviewAccessRepository.DisplayReviewAccess;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DisplayReviewAccessJpaAdapter implements DisplayReviewAccessRepository {
  private static final String PUBLISHED = "PUBLISHED";

  private final DisplayExistenceJpaRepository displayRepository;
  private final DisplayReviewTeamMemberReferenceJpaRepository teamMemberRepository;

  @Override
  public Optional<DisplayReviewAccess> findByDisplayIdAndUserId(Long displayId, Long userId) {
    return displayRepository
        .findById(displayId)
        .map(
            display ->
                new DisplayReviewAccess(
                    display.getOwnerUserId(),
                    display.getStartDate(),
                    display.getEndDate(),
                    PUBLISHED.equals(display.getStatus()),
                    teamMemberRepository.existsByDisplayIdAndUserIdAndAcceptedTrue(
                        displayId, userId)));
  }
}
