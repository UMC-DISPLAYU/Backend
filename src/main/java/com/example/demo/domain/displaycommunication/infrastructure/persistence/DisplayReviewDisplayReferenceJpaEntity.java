package com.example.demo.domain.displaycommunication.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@Entity
@Table(name = "Display")
public class DisplayReviewDisplayReferenceJpaEntity {
  @Id
  @Column(name = "displayId")
  private Long displayId;

  @Column(name = "userId")
  private Long ownerUserId;

  @Column(name = "startDate")
  private LocalDate startDate;

  @Column(name = "endDate")
  private LocalDate endDate;

  @Column(name = "status")
  private String status;

  protected DisplayReviewDisplayReferenceJpaEntity() {}
}
