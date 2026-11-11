package com.example.demo.domain.user.domain.entity;

import com.example.demo.domain.user.domain.aggregate.University;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "UniversityAlias")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UniversityAlias {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "aliasId")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "universityId", nullable = false)
  private University university;

  @Column(name = "aliasName", nullable = false, length = 100)
  private String name;
}
