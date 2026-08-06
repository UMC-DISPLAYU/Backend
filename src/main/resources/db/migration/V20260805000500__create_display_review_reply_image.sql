CREATE TABLE DisplayReviewReplyImage
(
    displayReviewReplyImageId BIGINT AUTO_INCREMENT PRIMARY KEY,
    imageUrl                  TEXT                               NOT NULL,
    width                     INT                                NOT NULL,
    height                    INT                                NOT NULL,
    sortOrder                 INT                                NOT NULL,
    createdAt                 DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updatedAt                 DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    displayReviewReplyId      BIGINT                             NOT NULL,
    CONSTRAINT FK_DISPLAY_REVIEW_REPLY_IMAGE_REPLY
        FOREIGN KEY (displayReviewReplyId)
            REFERENCES DisplayReviewReply (displayReviewReplyId)
);

CREATE INDEX IDX_DISPLAY_REVIEW_REPLY_IMAGE_REPLY
    ON DisplayReviewReplyImage (displayReviewReplyId, sortOrder);
