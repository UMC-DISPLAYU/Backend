CREATE TABLE DisplayReviewLike
(
    displayReviewLikeId BIGINT AUTO_INCREMENT PRIMARY KEY,
    createdAt           DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updatedAt           DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deletedAt           DATETIME                           NULL,
    displayReviewId     BIGINT                             NOT NULL,
    userId              BIGINT                             NOT NULL,
    CONSTRAINT UQ_DISPLAY_REVIEW_LIKE_REVIEW_USER
        UNIQUE (displayReviewId, userId),
    CONSTRAINT FK_DISPLAY_REVIEW_LIKE_REVIEW
        FOREIGN KEY (displayReviewId) REFERENCES DisplayReview (displayReviewId)
);

CREATE INDEX IDX_DISPLAY_REVIEW_LIKE_REVIEW
    ON DisplayReviewLike (displayReviewId, deletedAt);
