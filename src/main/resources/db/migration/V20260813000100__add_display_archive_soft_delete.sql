ALTER TABLE `Display`
    ADD COLUMN `deletedAt` DATETIME NULL;

ALTER TABLE `ArchiveDisplay`
    ADD COLUMN `deletedAt` DATETIME NULL;

ALTER TABLE `ArchiveWork`
    ADD COLUMN `deletedAt` DATETIME NULL;

ALTER TABLE `ArchivePersonalWork`
    ADD COLUMN `deletedAt` DATETIME NULL;

ALTER TABLE `ArchiveArtist`
    ADD COLUMN `deletedAt` DATETIME NULL;

ALTER TABLE `ArchiveDisplay`
    DROP INDEX `UQ_ARCHIVEDISPLAY_USER_DISPLAY`,
    ADD COLUMN `activeDisplayId` BIGINT
        GENERATED ALWAYS AS (
            IF(`deletedAt` IS NULL, `displayId`, NULL)
        ) STORED,
    ADD CONSTRAINT `UQ_ARCHIVEDISPLAY_ACTIVE_USER_DISPLAY`
        UNIQUE (`userId`, `activeDisplayId`);

ALTER TABLE `ArchiveWork`
    DROP INDEX `UQ_ARCHIVEWORK_USER_DISPLAYARTWORK`,
    ADD COLUMN `activeDisplayArtworkId` BIGINT
        GENERATED ALWAYS AS (
            IF(`deletedAt` IS NULL, `displayArtworkId`, NULL)
        ) STORED,
    ADD CONSTRAINT `UQ_ARCHIVEWORK_ACTIVE_USER_DISPLAYARTWORK`
        UNIQUE (`userId`, `activeDisplayArtworkId`);

ALTER TABLE `ArchivePersonalWork`
    DROP INDEX `UQ_ARCHIVEPERSONALWORK_USER_PERSONALARTWORK`,
    ADD COLUMN `activePersonalArtworkId` BIGINT
        GENERATED ALWAYS AS (
            IF(`deletedAt` IS NULL, `personalArtworkId`, NULL)
        ) STORED,
    ADD CONSTRAINT `UQ_ARCHIVEPERSONALWORK_ACTIVE_USER_PERSONALARTWORK`
        UNIQUE (`userId`, `activePersonalArtworkId`);

ALTER TABLE `ArchiveArtist`
    DROP INDEX `UQ_ARCHIVEARTIST_USER_ARTISTPROFILE`,
    ADD COLUMN `activeArtistProfileId` BIGINT
        GENERATED ALWAYS AS (
            IF(`deletedAt` IS NULL, `artistProfileId`, NULL)
        ) STORED,
    ADD CONSTRAINT `UQ_ARCHIVEARTIST_ACTIVE_USER_ARTISTPROFILE`
        UNIQUE (`userId`, `activeArtistProfileId`);

CREATE INDEX `IDX_DISPLAY_DELETED_STATUS_ID`
    ON `Display` (`deletedAt`, `status`, `displayId`);

CREATE INDEX `IDX_ARCHIVEDISPLAY_USER_DELETED_SAVED_ID`
    ON `ArchiveDisplay` (`userId`, `deletedAt`, `savedAt`, `archiveDisplayId`);

CREATE INDEX `IDX_ARCHIVEDISPLAY_DISPLAY_DELETED`
    ON `ArchiveDisplay` (`displayId`, `deletedAt`);

CREATE INDEX `IDX_ARCHIVEWORK_USER_DELETED_SAVED_ID`
    ON `ArchiveWork` (`userId`, `deletedAt`, `savedAt`, `archiveWorkId`);

CREATE INDEX `IDX_ARCHIVEWORK_ARTWORK_DELETED`
    ON `ArchiveWork` (`displayArtworkId`, `deletedAt`);

CREATE INDEX `IDX_ARCHIVEPERSONALWORK_USER_DELETED_SAVED_ID`
    ON `ArchivePersonalWork` (`userId`, `deletedAt`, `savedAt`, `archivePersonalWorkId`);

CREATE INDEX `IDX_ARCHIVEPERSONALWORK_ARTWORK_DELETED`
    ON `ArchivePersonalWork` (`personalArtworkId`, `deletedAt`);

CREATE INDEX `IDX_ARCHIVEARTIST_USER_DELETED_SAVED_ID`
    ON `ArchiveArtist` (`userId`, `deletedAt`, `savedAt`, `archiveArtistId`);

CREATE INDEX `IDX_ARCHIVEARTIST_ARTIST_USER_DELETED`
    ON `ArchiveArtist` (`artistUserId`, `deletedAt`);
