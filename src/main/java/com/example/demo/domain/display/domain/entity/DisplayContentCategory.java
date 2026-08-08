package com.example.demo.domain.display.domain.entity;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.error.DisplayErrorCode;
import com.example.demo.domain.display.domain.type.DisplayContentStatus;
import com.example.demo.global.entity.BaseTimeEntity;
import com.example.demo.global.error.BusinessException;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
@Entity
@Table(name = "DisplayContentCategory")
public class DisplayContentCategory extends BaseTimeEntity {

  static final int MAX_CONTENT_COUNT = 20;

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

  public void addContent(DisplayContent content) {
    if (contents.size() >= MAX_CONTENT_COUNT) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_CONTENT_LIMIT_EXCEEDED);
    }
    DisplayContent displayContent = Objects.requireNonNull(content, "content must not be null.");
    displayContent.assignCategory(this);
    contents.add(displayContent);
  }

  public DisplayContent createContent(String imageUrl) {
    return createContent(imageUrl, DisplayContentStatus.PUBLISHED);
  }

  public DisplayContent createContent(String imageUrl, DisplayContentStatus status) {
    if (contents.size() >= MAX_CONTENT_COUNT) {
      throw new BusinessException(DisplayErrorCode.DISPLAY_CONTENT_LIMIT_EXCEEDED);
    }
    DisplayContent content = new DisplayContent(null, imageUrl, nextSortOrder(), status);
    addContent(content);
    return content;
  }

  public DisplayContent changeContent(Long contentId, String imageUrl) {
    DisplayContent content = findContent(contentId);
    content.changeImageUrl(imageUrl);
    return content;
  }

  public void removeContent(Long contentId) {
    DisplayContent content = findContent(contentId);
    contents.remove(content);
  }

  public DisplayContent findContent(Long contentId) {
    return contents.stream()
        .filter(content -> Objects.equals(content.getId(), contentId))
        .findFirst()
        .orElseThrow(() -> new BusinessException(DisplayErrorCode.DISPLAY_CONTENT_NOT_FOUND));
  }

  public void reorderContents(List<Long> orderedContentIds) {
    if (orderedContentIds == null || orderedContentIds.size() != contents.size()) {
      throw new BusinessException(DisplayErrorCode.INVALID_DISPLAY_CONTENT_ORDER);
    }

    Map<Long, DisplayContent> contentById =
        contents.stream().collect(Collectors.toMap(DisplayContent::getId, Function.identity()));
    Set<Long> orderedIdSet = new HashSet<>(orderedContentIds);
    if (orderedIdSet.size() != orderedContentIds.size()
        || !orderedIdSet.equals(contentById.keySet())) {
      throw new BusinessException(DisplayErrorCode.INVALID_DISPLAY_CONTENT_ORDER);
    }

    for (int index = 0; index < orderedContentIds.size(); index++) {
      contentById.get(orderedContentIds.get(index)).changeSortOrder(index);
    }
  }

  private int nextSortOrder() {
    return contents.stream().mapToInt(DisplayContent::getSortOrder).max().orElse(-1) + 1;
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
