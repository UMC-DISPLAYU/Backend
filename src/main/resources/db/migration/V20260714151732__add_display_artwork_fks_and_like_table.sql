ALTER TABLE `DisplayArtwork`
    ADD CONSTRAINT `FK_DISPLAYARTWORK_DISPLAY` FOREIGN KEY (`displayId`) REFERENCES `Display` (`displayId`);

ALTER TABLE `ArtworkImage`
    ADD CONSTRAINT `FK_ARTWORKIMAGE_DISPLAYARTWORK` FOREIGN KEY (`displayArtworkId`) REFERENCES `DisplayArtwork` (`displayArtworkId`);

ALTER TABLE `Creator`
    ADD CONSTRAINT `FK_CREATOR_DISPLAYARTWORK` FOREIGN KEY (`displayArtworkId`) REFERENCES `DisplayArtwork` (`displayArtworkId`);

ALTER TABLE `Creator`
    ADD CONSTRAINT `FK_CREATOR_USER` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`);

CREATE TABLE `DisplayArtworkLike` (
    `displayArtworkLikeId` BIGINT NOT NULL AUTO_INCREMENT,
    `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `displayArtworkId` BIGINT NOT NULL,
    `userId` BIGINT NOT NULL,
    CONSTRAINT `PK_DISPLAYARTWORKLIKE` PRIMARY KEY (`displayArtworkLikeId`),
    CONSTRAINT `UQ_DISPLAYARTWORKLIKE_ARTWORK_USER` UNIQUE (`displayArtworkId`, `userId`),
    CONSTRAINT `FK_DISPLAYARTWORKLIKE_DISPLAYARTWORK` FOREIGN KEY (`displayArtworkId`) REFERENCES `DisplayArtwork` (`displayArtworkId`),
    CONSTRAINT `FK_DISPLAYARTWORKLIKE_USER` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
);

-- PersonalArtwork 좋아요 기능은 이번 스프린트 범위 밖(API 미구현)이지만,
-- ERD 합의된 스키마를 미리 반영해둠 (추후 별도 PR에서 API 구현 예정).
CREATE TABLE `PersonalArtworkLike` (
    `personalArtworkLikeId` BIGINT NOT NULL AUTO_INCREMENT,
    `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `personalArtworkId` BIGINT NOT NULL,
    `userId` BIGINT NOT NULL,
    CONSTRAINT `PK_PERSONALARTWORKLIKE` PRIMARY KEY (`personalArtworkLikeId`),
    CONSTRAINT `UQ_PERSONALARTWORKLIKE_ARTWORK_USER` UNIQUE (`personalArtworkId`, `userId`),
    CONSTRAINT `FK_PERSONALARTWORKLIKE_PERSONALARTWORK` FOREIGN KEY (`personalArtworkId`) REFERENCES `PersonalArtwork` (`personalArtworkId`),
    CONSTRAINT `FK_PERSONALARTWORKLIKE_USER` FOREIGN KEY (`userId`) REFERENCES `User` (`userId`)
);
