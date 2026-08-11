CREATE TABLE PersonalArtworkQuestionImage
(
    personalQuestionImageId BIGINT AUTO_INCREMENT PRIMARY KEY,
    imageUrl                TEXT                               NOT NULL,
    width                   INT                                NOT NULL,
    height                  INT                                NOT NULL,
    sortOrder               INT                                NOT NULL,
    createdAt               DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updatedAt               DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    personalQuestionId      BIGINT                             NOT NULL,
    CONSTRAINT FK_PERSONAL_ARTWORK_QUESTION_IMAGE_QUESTION
        FOREIGN KEY (personalQuestionId)
            REFERENCES PersonalArtworkQuestion (personalQuestionId)
);

CREATE INDEX IDX_PERSONAL_ARTWORK_QUESTION_IMAGE_QUESTION_SORT
    ON PersonalArtworkQuestionImage (personalQuestionId, sortOrder);

CREATE TABLE PersonalArtworkQuestionReplyImage
(
    personalQuestionReplyImageId BIGINT AUTO_INCREMENT PRIMARY KEY,
    imageUrl                     TEXT                               NOT NULL,
    width                        INT                                NOT NULL,
    height                       INT                                NOT NULL,
    sortOrder                    INT                                NOT NULL,
    createdAt                    DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updatedAt                    DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    personalQuestionReplyId      BIGINT                             NOT NULL,
    CONSTRAINT FK_PERSONAL_ARTWORK_QUESTION_REPLY_IMAGE_REPLY
        FOREIGN KEY (personalQuestionReplyId)
            REFERENCES PersonalArtworkQuestionReply (personalQuestionReplyId)
);

CREATE INDEX IDX_PERSONAL_ARTWORK_QUESTION_REPLY_IMAGE_REPLY_SORT
    ON PersonalArtworkQuestionReplyImage (personalQuestionReplyId, sortOrder);
