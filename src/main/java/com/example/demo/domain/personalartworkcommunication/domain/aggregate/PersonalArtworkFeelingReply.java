package com.example.demo.domain.personalartworkcommunication.domain.aggregate;

import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "PersonalArtworkFeelingReply")
public class PersonalArtworkFeelingReply extends SoftDeleteBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalFeelingReplyId")
  private Long personalFeelingReplyId;

  @Column(name = "content", nullable = false, length = 300)
  private String content;

  @Column(name = "userId", nullable = false)
  private Long userId;

  @Column(name = "personalFeelingId", nullable = false)
  private Long personalFeelingId;

  protected PersonalArtworkFeelingReply() {}

  private PersonalArtworkFeelingReply(
      Long personalFeelingReplyId, String content, Long userId, Long personalFeelingId) {
    this.personalFeelingReplyId = personalFeelingReplyId;
    this.content = content;
    this.personalFeelingId = personalFeelingId;
    this.userId = userId;
  }

  public static PersonalArtworkFeelingReply create(
      Long personalFeelingId, Long userId, String content) {
    return new PersonalArtworkFeelingReply(null, content, userId, personalFeelingId);
  }
}
