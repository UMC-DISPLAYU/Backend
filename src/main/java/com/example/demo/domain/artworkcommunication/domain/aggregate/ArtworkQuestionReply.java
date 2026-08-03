package com.example.demo.domain.artworkcommunication.domain.aggregate;

import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "ArtworkQuestionReply")
public class ArtworkQuestionReply extends SoftDeleteBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "queReplyId")
  private Long queReplyId;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "questionId", nullable = false)
  private Long questionId;

  @Column(name = "creatorId")
  private Long creatorId;

  protected ArtworkQuestionReply() {}

  private ArtworkQuestionReply(Long queReplyId, String content, Long questionId, Long creatorId) {
    this.queReplyId = queReplyId;
    this.content = content;
    this.questionId = questionId;
    this.creatorId = creatorId;
  }

  public static ArtworkQuestionReply create(Long questionId, String content, Long creatorId) {
    return new ArtworkQuestionReply(null, content, questionId, creatorId);
  }

  public boolean belongsToQuestion(Long questionId) {
    return this.questionId.equals(questionId);
  }

  public boolean isWrittenBy(Long creatorId) {
    return this.creatorId.equals(creatorId);
  }
}
