package com.example.demo.domain.personalartworkcommunication.domain.aggregate;

import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "PersonalArtworkQuestionReply")
public class PersonalArtworkQuestionReply extends SoftDeleteBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalQuestionReplyId")
  private Long personalQuestionReplyId;

  @Column(name = "content", nullable = false, length = 300)
  private String content;

  @Column(name = "userId", nullable = false)
  private Long userId;

  @Column(name = "personalQuestionId", nullable = false)
  private Long personalQuestionId;

  protected PersonalArtworkQuestionReply() {}

  private PersonalArtworkQuestionReply(
      Long personalQuestionReplyId, String content, Long userId, Long personalQuestionId) {
    this.personalQuestionReplyId = personalQuestionReplyId;
    this.content = content;
    this.personalQuestionId = personalQuestionId;
    this.userId = userId;
  }

  public static PersonalArtworkQuestionReply create(
      Long personalQuestionId, Long userId, String content) {
    return new PersonalArtworkQuestionReply(null, content, userId, personalQuestionId);
  }
}
