CREATE TABLE `LoungePostImage`
(
    `loungePostImageId` BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    `loungePostId`      BIGINT                             NOT NULL,
    `imageUrl`          TEXT                               NOT NULL,
    `sortOrder`         INT                                NOT NULL,
    `createdAt`         DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updatedAt`         DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT `FK_LOUNGEPOSTIMAGE_LOUNGEPOST`
        FOREIGN KEY (`loungePostId`) REFERENCES `LoungePost` (`loungePostId`)
);

INSERT INTO `LoungePostImage` (
    `loungePostId`,
    `imageUrl`,
    `sortOrder`,
    `createdAt`,
    `updatedAt`
)
SELECT
    `loungePostId`,
    `postImageUrl`,
    0,
    `createdAt`,
    `updatedAt`
FROM `LoungePost`
WHERE `postImageUrl` IS NOT NULL
  AND TRIM(`postImageUrl`) <> '';
