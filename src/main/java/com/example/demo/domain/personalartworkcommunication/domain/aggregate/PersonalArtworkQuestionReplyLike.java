package com.example.demo.domain.personalartworkcommunication.domain.aggregate;

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
@Table(name = "PersonalArtworkQuestionReplyLike")
public class PersonalArtworkQuestionReplyLike extends SoftDeleteBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalQuestionReplyLikeId")
  private Long personalQuestionReplyLikeId;

  @Column(name = "personalQuestionReplyId", nullable = false)
  private Long personalQuestionReplyId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  protected PersonalArtworkQuestionReplyLike() {}
}
