-- DisplayLike와 동일한 소프트딜리트+복원(cancel/restore) 패턴으로 통일
ALTER TABLE `DisplayArtworkLike`
    ADD COLUMN `updatedAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `createdAt`,
    ADD COLUMN `deletedAt` DATETIME NULL AFTER `updatedAt`;

ALTER TABLE `PersonalArtworkLike`
    ADD COLUMN `updatedAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `createdAt`,
    ADD COLUMN `deletedAt` DATETIME NULL AFTER `updatedAt`;
