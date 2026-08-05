CREATE TABLE `PersonalArtworkQuestionReplyLike`
(
    `personalQuestionReplyLikeId` BIGINT NOT NULL AUTO_INCREMENT,
    `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deletedAt` DATETIME NULL,
    `personalQuestionReplyId` BIGINT NOT NULL,
    `userId` BIGINT NOT NULL,
    CONSTRAINT `PK_PERSONALARTWORKQUESTIONREPLYLIKE`
        PRIMARY KEY (`personalQuestionReplyLikeId`),
    CONSTRAINT `UK_PERSONALARTWORKQUESTIONREPLYLIKE_REPLY_USER`
        UNIQUE (`personalQuestionReplyId`, `userId`),
    CONSTRAINT `FK_PERSONALARTWORKQUESTIONREPLYLIKE_REPLY`
        FOREIGN KEY (`personalQuestionReplyId`)
            REFERENCES `PersonalArtworkQuestionReply` (`personalQuestionReplyId`),
    CONSTRAINT `FK_PERSONALARTWORKQUESTIONREPLYLIKE_USER`
        FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
);

CREATE INDEX `IDX_PERSONALARTWORKQUESTIONREPLYLIKE_REPLY`
    ON `PersonalArtworkQuestionReplyLike` (`personalQuestionReplyId`, `deletedAt`);
