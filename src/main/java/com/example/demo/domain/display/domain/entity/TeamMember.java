package com.example.demo.domain.display.domain.entity;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
@Table(name = "TeamMember")
public class TeamMember {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "teamId")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "displayId", nullable = false)
  private Display display;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "userId", nullable = false))
  private UserId userId;

  @Column(nullable = false)
  private String displayNickname;

  @Enumerated(EnumType.STRING)
  @Column(name = "author", nullable = false)
  private TeamMemberRole role;

  @Column(name = "isAccepted", nullable = false)
  private boolean accepted;

  protected TeamMember() {}

  public TeamMember(
      Long id, UserId userId, String displayNickname, TeamMemberRole role, boolean accepted) {
    this.id = id;
    this.userId = Objects.requireNonNull(userId, "userId must not be null.");
    this.displayNickname = requireNonBlank(displayNickname, "displayNickname");
    this.role = Objects.requireNonNull(role, "role must not be null.");
    this.accepted = accepted;
  }

  public void assignDisplay(Display display) {
    this.display = Objects.requireNonNull(display, "display must not be null.");
  }

  public void changeDisplayNickname(String displayNickname) {
    this.displayNickname = requireNonBlank(displayNickname, "displayNickname");
  }

  public void changeRole(TeamMemberRole role) {
    this.role = Objects.requireNonNull(role, "role must not be null.");
  }

  public void accept() {
    this.accepted = true;
  }

  public void reject() {
    this.accepted = false;
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank.");
    }
    return value;
  }
}
