UPDATE `Memo` m1
INNER JOIN `Memo` m2
  ON m1.`archiveDisplayId` = m2.`archiveDisplayId`
  AND m1.`archiveDisplayId` IS NOT NULL
  AND m1.`deletedAt` IS NULL
  AND m2.`deletedAt` IS NULL
  AND m1.`memoId` < m2.`memoId`
SET m1.`deletedAt` = NOW(6);

UPDATE `Memo` m1
INNER JOIN `Memo` m2
  ON m1.`archiveWorkId` = m2.`archiveWorkId`
  AND m1.`archiveWorkId` IS NOT NULL
  AND m1.`deletedAt` IS NULL
  AND m2.`deletedAt` IS NULL
  AND m1.`memoId` < m2.`memoId`
SET m1.`deletedAt` = NOW(6);

ALTER TABLE `Memo`
  ADD COLUMN `activeArchiveDisplayId` BIGINT
    GENERATED ALWAYS AS (IF(`deletedAt` IS NULL, `archiveDisplayId`, NULL)) VIRTUAL,
  ADD COLUMN `activeArchiveWorkId` BIGINT
    GENERATED ALWAYS AS (IF(`deletedAt` IS NULL, `archiveWorkId`, NULL)) VIRTUAL;

ALTER TABLE `Memo`
  ADD CONSTRAINT `UQ_MEMO_ACTIVE_ARCHIVEDISPLAY` UNIQUE (`activeArchiveDisplayId`),
  ADD CONSTRAINT `UQ_MEMO_ACTIVE_ARCHIVEWORK` UNIQUE (`activeArchiveWorkId`);
