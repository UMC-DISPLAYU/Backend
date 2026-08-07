CREATE TABLE ArtworkFeelingReplyImage
(
    feelingReplyImageId BIGINT AUTO_INCREMENT PRIMARY KEY,
    imageUrl            TEXT                               NOT NULL,
    width               INT                                NOT NULL,
    height              INT                                NOT NULL,
    sortOrder           INT                                NOT NULL,
    createdAt           DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updatedAt           DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    feelingReplyId      BIGINT                             NOT NULL,
    CONSTRAINT FK_ARTWORK_FEELING_REPLY_IMAGE_REPLY
        FOREIGN KEY (feelingReplyId)
            REFERENCES ArtworkFeelingReply (feelingReplyId)
);

CREATE INDEX IDX_ARTWORK_FEELING_REPLY_IMAGE_REPLY
    ON ArtworkFeelingReplyImage (feelingReplyId, sortOrder);
