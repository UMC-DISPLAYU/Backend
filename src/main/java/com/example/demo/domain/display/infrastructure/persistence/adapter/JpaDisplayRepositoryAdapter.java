package com.example.demo.domain.display.infrastructure.persistence.adapter;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDisplayRepositoryAdapter implements DisplayRepository {

  private final SpringDataDisplayJpaRepository jpaRepository;

  public JpaDisplayRepositoryAdapter(SpringDataDisplayJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<Display> findById(Long displayId) {
    return jpaRepository.findById(displayId);
  }

  @Override
  public Optional<Display> findByIdWithOptimisticLock(Long displayId) {
    return jpaRepository.findWithOptimisticLockById(displayId);
  }

  @Override
  public Optional<Display> findByInvitationToken(String invitationTokenHash) {
    return jpaRepository.findByInvitationToken(invitationTokenHash);
  }

  @Override
  public List<Display> findCreatedDisplaysByUserId(Long userId) {
    return jpaRepository.findCreatedDisplaysByUserId(userId);
  }

  @Override
  public List<Display> findParticipatedDisplaysByUserId(Long userId) {
    return jpaRepository.findParticipatedDisplaysByUserId(userId);
  }

  @Override
  public List<Display> findPublishedCreatedDisplaysByUserId(Long userId) {
    return jpaRepository.findPublishedCreatedDisplaysByUserId(userId);
  }

  @Override
  public List<Display> findPublishedParticipatedDisplaysByUserId(Long userId) {
    return jpaRepository.findPublishedParticipatedDisplaysByUserId(userId);
  }

  @Override
  public List<Display> findAll() {
    return jpaRepository.findAll();
  }

  @Override
  public Display save(Display display) {
    return jpaRepository.save(display);
  }

  @Override
  public void flush() {
    jpaRepository.flush();
  }

  @Override
  public void delete(Display display) {
    jpaRepository.delete(display);
  }
}
