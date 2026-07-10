package com.example.demo.domain.display.domain.entity;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.DisplayField;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(name = "DisplayField")
public class DisplayFieldSelection {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "displayFieldId")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "displayId", nullable = false)
  private Display display;

  @Enumerated(EnumType.STRING)
  @Column(name = "field", nullable = false)
  private DisplayField field;

  @Column(nullable = false)
  private int sortOrder;

  protected DisplayFieldSelection() {}

  public DisplayFieldSelection(Long id, DisplayField field, int sortOrder) {
    this.id = id;
    this.field = Objects.requireNonNull(field, "field must not be null.");
    this.sortOrder = requireNonNegative(sortOrder, "sortOrder");
  }

  public void assignDisplay(Display display) {
    this.display = Objects.requireNonNull(display, "display must not be null.");
  }

  private static int requireNonNegative(int value, String fieldName) {
    if (value < 0) {
      throw new IllegalArgumentException(fieldName + " must not be negative.");
    }
    return value;
  }
}
