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

  @Column(name = "artQueId", nullable = false)
  private Long artQueId;

  protected ArtworkQuestionReply() {}

  private ArtworkQuestionReply(Long queReplyId, String content, Long artQueId) {
    this.queReplyId = queReplyId;
    this.content = content;
    this.artQueId = artQueId;
  }

  public static ArtworkQuestionReply create(Long artQueId, String content) {
    return new ArtworkQuestionReply(null, content, artQueId);
  }
}
