package com.example.demo.domain.display.domain.entity;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(name = "DisplayContentCategory")
public class DisplayContentCategory extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "categoryId")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "displayId", nullable = false)
  private Display display;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @Column(name = "categorySortOrder", nullable = false)
  private int sortOrder;

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<DisplayContent> contents = new ArrayList<>();

  protected DisplayContentCategory() {}

  public DisplayContentCategory(
      Long id, String name, String description, int sortOrder, List<DisplayContent> contents) {
    this.id = id;
    changeInfo(name, description);
    this.sortOrder = requireNonNegative(sortOrder, "sortOrder");
    if (contents != null) {
      contents.forEach(this::addContent);
    }
  }

  public void assignDisplay(Display display) {
    this.display = Objects.requireNonNull(display, "display must not be null.");
  }

  public List<DisplayContent> getContents() {
    return Collections.unmodifiableList(contents);
  }

  public void changeInfo(String name, String description) {
    this.name = requireNonBlank(name, "name");
    this.description = requireNonBlank(description, "description");
  }

  public void changeSortOrder(int sortOrder) {
    this.sortOrder = requireNonNegative(sortOrder, "sortOrder");
  }

  public void addContent(DisplayContent content) {
    DisplayContent displayContent = Objects.requireNonNull(content, "content must not be null.");
    displayContent.assignCategory(this);
    contents.add(displayContent);
  }

  public void removeContent(Long contentId) {
    contents.removeIf(content -> Objects.equals(content.getId(), contentId));
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank.");
    }
    return value;
  }

  private static int requireNonNegative(int value, String fieldName) {
    if (value < 0) {
      throw new IllegalArgumentException(fieldName + " must not be negative.");
    }
    return value;
  }
}
