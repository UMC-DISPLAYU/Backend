package com.example.demo.domain.lounge.domain.entity;

import com.example.demo.domain.lounge.domain.type.LoungeCommentStatus;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Objects;

@Getter
@Entity
@Table(name = "LoungeComment")
public class LoungeComment extends SoftDeleteBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loungeCommentId")
    private Long id;

    @Column(nullable = false)
    private Long loungePostId;

    @Column(name = "parentCommentId")
    private Long parentCommentId;

    @Embedded
    @AttributeOverride(name="value", column = @Column(name="userId",nullable = false))
    private UserId authorUserId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "commentStatus", nullable = false)
    private LoungeCommentStatus status;

    protected LoungeComment() {}

    public static LoungeComment createComment(
            Long loungePostId, UserId authorUserId, String content){
        return new LoungeComment(null, loungePostId,null, authorUserId,content,LoungeCommentStatus.ACTIVE);
    }
    public static LoungeComment createReply(
            Long loungePostId, Long parentCommentId, UserId authorUserId, String content) {
        return new LoungeComment(
                null, loungePostId, parentCommentId, authorUserId, content, LoungeCommentStatus.ACTIVE);
    }

    public LoungeComment(
            Long id,
            Long loungePostId,
            Long parentCommentId,
            UserId authorUserId,
            String content,
            LoungeCommentStatus status) {
        this.id = id;
        this.loungePostId = requirePositive(loungePostId, "loungePostId");
        this.parentCommentId = parentCommentId == null
                ? null
                : requirePositive(parentCommentId, "parentCommentId");
        this.authorUserId = Objects.requireNonNull(authorUserId, "authorUserId must not be null.");
        changeContent(content);
        this.status = Objects.requireNonNull(status, "status must not be null.");
    }
    public void changeContent(String content) {
        this.content = requireNonBlank(content, "content");
    }

    public boolean isReply() {
        return parentCommentId != null;
    }

    public boolean isRootComment() {
        return parentCommentId == null;
    }

    public boolean isActive() {
        return status == LoungeCommentStatus.ACTIVE;
    }

    @Override
    public void delete() {
        this.status = LoungeCommentStatus.DELETED;
        super.delete();
    }

    @Override
    public void restore() {
        this.status = LoungeCommentStatus.ACTIVE;
        super.restore();
    }

    private static Long requirePositive(Long value, String fieldName) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive.");
        }
        return value;
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
        return value;
    }
}

