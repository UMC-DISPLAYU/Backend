CREATE TABLE `PersonalArtworkFeelingReplyLike`
(
    `personalFeelingReplyLikeId` BIGINT NOT NULL AUTO_INCREMENT,
    `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `deletedAt` DATETIME NULL,
    `personalFeelingReplyId` BIGINT NOT NULL,
    `userId` BIGINT NOT NULL,
    CONSTRAINT `PK_PERSONALARTWORKFEELINGREPLYLIKE`
        PRIMARY KEY (`personalFeelingReplyLikeId`),
    CONSTRAINT `UK_PERSONALARTWORKFEELINGREPLYLIKE_REPLY_USER`
        UNIQUE (`personalFeelingReplyId`, `userId`),
    CONSTRAINT `FK_PERSONALARTWORKFEELINGREPLYLIKE_REPLY`
        FOREIGN KEY (`personalFeelingReplyId`)
            REFERENCES `PersonalArtworkFeelingReply` (`personalFeelingReplyId`),
    CONSTRAINT `FK_PERSONALARTWORKFEELINGREPLYLIKE_USER`
        FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
);

CREATE INDEX `IDX_PERSONALARTWORKFEELINGREPLYLIKE_REPLY`
    ON `PersonalArtworkFeelingReplyLike` (`personalFeelingReplyId`, `deletedAt`);
