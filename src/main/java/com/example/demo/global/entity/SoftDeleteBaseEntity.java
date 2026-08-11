package com.example.demo.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class SoftDeleteBaseEntity extends BaseTimeEntity {

  @Column private LocalDateTime deletedAt;

  public void delete() {
    this.deletedAt = LocalDateTime.now(ZoneOffset.UTC);
  }

  public void restore() {
    this.deletedAt = null;
  }

  public boolean isDeleted() {
    return deletedAt != null;
  }
}
