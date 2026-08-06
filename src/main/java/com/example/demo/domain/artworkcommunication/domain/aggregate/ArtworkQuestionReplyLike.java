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
@Table(name = "ArtworkQuestionReplyLike")
public class ArtworkQuestionReplyLike extends SoftDeleteBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "questionReplyLikeId")
  private Long questionReplyLikeId;

  @Column(name = "questionReplyId", nullable = false)
  private Long questionReplyId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  protected ArtworkQuestionReplyLike() {}
}
