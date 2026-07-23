package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TeamMember")
public class DisplayReviewTeamMemberReferenceJpaEntity {
  @Id
  @Column(name = "teamId")
  private Long teamId;

  @Column(name = "displayId")
  private Long displayId;

  @Column(name = "userId")
  private Long userId;

  @Column(name = "isAccepted")
  private boolean accepted;

  protected DisplayReviewTeamMemberReferenceJpaEntity() {}
}
