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
@Table(name = "Agreement")
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

  @Column(name = "code")
  private String code;

  @Column(name = "version")
  private String version;

  @Column(name = "isActive")
  private Boolean isActive;

  @Column(name = "effectiveDate")
  private LocalDate effectiveDate;

  @Column(name = "displayOrder")
  private Integer displayOrder;
}
