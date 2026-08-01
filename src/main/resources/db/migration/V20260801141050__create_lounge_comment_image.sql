CREATE TABLE `LoungeCommentImage`
(
    `loungeCommentImageId` BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    `imageUrl`             TEXT                               NOT NULL,
    `sortOrder`            INT                                NOT NULL,
    `createdAt`            DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updatedAt`            DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `loungeCommentId`      BIGINT                             NOT NULL,
    CONSTRAINT `FK_LOUNGECOMMENTIMAGE_LOUNGECOMMENT`
        FOREIGN KEY (`loungeCommentId`) REFERENCES `LoungeComment` (`loungeCommentId`)
);

CREATE INDEX `IDX_LOUNGECOMMENTIMAGE_COMMENT_SORT`
    ON `LoungeCommentImage` (`loungeCommentId`, `sortOrder`);
