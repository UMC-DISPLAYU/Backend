CREATE TABLE DisplayReviewReplyLike
(
    displayReviewReplyLikeId BIGINT AUTO_INCREMENT PRIMARY KEY,
    createdAt                DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updatedAt                DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deletedAt                DATETIME                           NULL,
    displayReviewReplyId     BIGINT                             NOT NULL,
    userId                   BIGINT                             NOT NULL,
    CONSTRAINT UQ_DISPLAY_REVIEW_REPLY_LIKE_REPLY_USER
        UNIQUE (displayReviewReplyId, userId),
    CONSTRAINT FK_DISPLAY_REVIEW_REPLY_LIKE_REPLY
        FOREIGN KEY (displayReviewReplyId)
            REFERENCES DisplayReviewReply (displayReviewReplyId)
);

CREATE INDEX IDX_DISPLAY_REVIEW_REPLY_LIKE_REPLY
    ON DisplayReviewReplyLike (displayReviewReplyId, deletedAt);
