CREATE TABLE PersonalArtworkFeelingReplyImage
(
    personalFeelingReplyImageId BIGINT AUTO_INCREMENT PRIMARY KEY,
    imageUrl                    TEXT                               NOT NULL,
    width                       INT                                NOT NULL,
    height                      INT                                NOT NULL,
    sortOrder                   INT                                NOT NULL,
    createdAt                   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updatedAt                   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    personalFeelingReplyId      BIGINT                             NOT NULL,
    CONSTRAINT FK_PERSONAL_ARTWORK_FEELING_REPLY_IMAGE_REPLY
        FOREIGN KEY (personalFeelingReplyId)
            REFERENCES PersonalArtworkFeelingReply (personalFeelingReplyId)
);

CREATE INDEX IDX_PERSONAL_ARTWORK_FEELING_REPLY_IMAGE_REPLY
    ON PersonalArtworkFeelingReplyImage (personalFeelingReplyId, sortOrder);
