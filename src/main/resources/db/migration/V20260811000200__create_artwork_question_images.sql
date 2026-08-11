CREATE TABLE ArtworkQuestionImage
(
    questionImageId BIGINT AUTO_INCREMENT PRIMARY KEY,
    imageUrl        TEXT                               NOT NULL,
    width           INT                                NOT NULL,
    height          INT                                NOT NULL,
    sortOrder       INT                                NOT NULL,
    createdAt       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updatedAt       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    questionId      BIGINT                             NOT NULL,
    CONSTRAINT FK_ARTWORK_QUESTION_IMAGE_QUESTION
        FOREIGN KEY (questionId)
            REFERENCES ArtworkQuestion (questionId)
);

CREATE INDEX IDX_ARTWORK_QUESTION_IMAGE_QUESTION_SORT
    ON ArtworkQuestionImage (questionId, sortOrder);

CREATE TABLE ArtworkQuestionReplyImage
(
    questionReplyImageId BIGINT AUTO_INCREMENT PRIMARY KEY,
    imageUrl             TEXT                               NOT NULL,
    width                INT                                NOT NULL,
    height               INT                                NOT NULL,
    sortOrder            INT                                NOT NULL,
    createdAt            DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updatedAt            DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    questionReplyId      BIGINT                             NOT NULL,
    CONSTRAINT FK_ARTWORK_QUESTION_REPLY_IMAGE_REPLY
        FOREIGN KEY (questionReplyId)
            REFERENCES ArtworkQuestionReply (queReplyId)
);

CREATE INDEX IDX_ARTWORK_QUESTION_REPLY_IMAGE_REPLY_SORT
    ON ArtworkQuestionReplyImage (questionReplyId, sortOrder);
