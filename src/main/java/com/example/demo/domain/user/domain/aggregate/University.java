package com.example.demo.domain.user.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "University")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class University {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "universityId")
  private Long id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "active", nullable = false)
  private boolean active;
}
