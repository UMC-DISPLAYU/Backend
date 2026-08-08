package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.repository.DisplayInvitationRepository;
import com.example.demo.domain.display.domain.type.DisplayInvitationStatus;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayInvitationJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaDisplayInvitationRepositoryAdapter implements DisplayInvitationRepository {

  private final SpringDataDisplayInvitationJpaRepository jpaRepository;

  public JpaDisplayInvitationRepositoryAdapter(
      SpringDataDisplayInvitationJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<DisplayInvitation> findById(Long invitationId) {
    return jpaRepository.findById(invitationId);
  }

  @Override
  public Optional<DisplayInvitation> findByIdForUpdate(Long invitationId) {
    return jpaRepository.findByIdForUpdate(invitationId);
  }

  @Override
  public boolean existsPendingByDisplayIdAndInviteeUserId(Long displayId, Long inviteeUserId) {
    return jpaRepository.existsByDisplayIdAndInviteeUserIdValueAndStatusAndDeletedAtIsNull(
        displayId, inviteeUserId, DisplayInvitationStatus.PENDING);
  }

  @Override
  public List<DisplayInvitation> findPendingByInviteeUserId(Long inviteeUserId) {
    return jpaRepository.findByInviteeUserIdValueAndStatusAndDeletedAtIsNullOrderByIdDesc(
        inviteeUserId, DisplayInvitationStatus.PENDING);
  }

  @Override
  public DisplayInvitation save(DisplayInvitation invitation) {
    return jpaRepository.saveAndFlush(invitation);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public DisplayInvitation saveInNewTransaction(DisplayInvitation invitation) {
    return jpaRepository.saveAndFlush(invitation);
  }
}
