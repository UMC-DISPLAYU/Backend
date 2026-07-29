package com.example.demo.domain.user.domain.entity;

import com.example.demo.domain.user.domain.enums.Type;
import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "Agreement",
    uniqueConstraints =
        @UniqueConstraint(
            name = "idx_agreement_code_version",
            columnNames = {"code", "version"}))
public class Agreement extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "agreeId")
  private Long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private Type type;

  @Column(name = "content", columnDefinition = "TEXT")
  private String content;

  @Column(name = "isRequired", nullable = false)
  private boolean isRequired;

  @Column(name = "code", nullable = false, length = 100)
  private String code;

  @Column(name = "version", nullable = false, length = 32)
  private String version;

  @Column(name = "isActive", nullable = false)
  private boolean isActive;

  @Column(name = "effectiveDate", nullable = false)
  private LocalDate effectiveDate;

  @Column(name = "displayOrder", nullable = false)
  private Integer displayOrder;
}
