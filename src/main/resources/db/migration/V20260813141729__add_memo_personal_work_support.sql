ALTER TABLE `Memo`
    ADD COLUMN `archivePersonalWorkId` BIGINT NULL;

ALTER TABLE `Memo`
    ADD COLUMN `activeArchivePersonalWorkId` BIGINT
        GENERATED ALWAYS AS (
            IF(`deletedAt` IS NULL, `archivePersonalWorkId`, NULL)
        ) STORED;

ALTER TABLE `Memo`
    ADD CONSTRAINT `UQ_MEMO_ACTIVE_ARCHIVEPERSONALWORK`
        UNIQUE (`activeArchivePersonalWorkId`);

ALTER TABLE `Memo`
    ADD CONSTRAINT `FK_MEMO_ARCHIVEPERSONALWORK`
        FOREIGN KEY (`archivePersonalWorkId`) REFERENCES `ArchivePersonalWork` (`archivePersonalWorkId`);
