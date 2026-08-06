CREATE TABLE `PersonalArtworkQuestionLike`
(
    `personalQuestionLikeId` BIGINT NOT NULL AUTO_INCREMENT,
    `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deletedAt` DATETIME NULL,
    `personalQuestionId` BIGINT NOT NULL,
    `userId` BIGINT NOT NULL,
    CONSTRAINT `PK_PERSONALARTWORKQUESTIONLIKE`
        PRIMARY KEY (`personalQuestionLikeId`),
    CONSTRAINT `UK_PERSONALARTWORKQUESTIONLIKE_QUESTION_USER`
        UNIQUE (`personalQuestionId`, `userId`),
    CONSTRAINT `FK_PERSONALARTWORKQUESTIONLIKE_QUESTION`
        FOREIGN KEY (`personalQuestionId`)
            REFERENCES `PersonalArtworkQuestion` (`personalQuestionId`),
    CONSTRAINT `FK_PERSONALARTWORKQUESTIONLIKE_USER`
        FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
);

CREATE INDEX `IDX_PERSONALARTWORKQUESTIONLIKE_QUESTION`
    ON `PersonalArtworkQuestionLike` (`personalQuestionId`, `deletedAt`);
