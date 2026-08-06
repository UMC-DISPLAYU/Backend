package com.example.demo.domain.archive.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "Display")
public class ArchiveDisplayReferenceJpaEntity {

  @Id
  @Column(name = "displayId")
  private Long displayId;

  @Column(name = "title")
  private String title;

  @Column(name = "organization")
  private String organization;

  @Column(name = "department")
  private String department;

  @Column(name = "placeName")
  private String placeName;

  @Column(name = "startDate")
  private LocalDate startDate;

  @Column(name = "endDate")
  private LocalDate endDate;

  protected ArchiveDisplayReferenceJpaEntity() {}

  public Long getDisplayId() {
    return displayId;
  }

  public String getTitle() {
    return title;
  }

  public String getOrganization() {
    return organization;
  }

  public String getDepartment() {
    return department;
  }

  public String getPlaceName() {
    return placeName;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }
}
