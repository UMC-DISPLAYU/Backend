CREATE TABLE DisplayReviewReply
(
    displayReviewReplyId BIGINT AUTO_INCREMENT PRIMARY KEY,
    content              VARCHAR(300)                       NOT NULL,
    createdAt            DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updatedAt            DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deletedAt            DATETIME                           NULL,
    displayReviewId      BIGINT                             NOT NULL,
    userId               BIGINT                             NOT NULL,
    CONSTRAINT FK_DISPLAY_REVIEW_REPLY_REVIEW
        FOREIGN KEY (displayReviewId) REFERENCES DisplayReview (displayReviewId)
);

CREATE INDEX IDX_DISPLAY_REVIEW_REPLY_REVIEW
    ON DisplayReviewReply (displayReviewId, deletedAt, displayReviewReplyId);
