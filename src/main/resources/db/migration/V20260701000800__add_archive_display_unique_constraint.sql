DELETE ad1 FROM `ArchiveDisplay` ad1
INNER JOIN `ArchiveDisplay` ad2
  ON ad1.`userId` = ad2.`userId`
  AND ad1.`displayId` = ad2.`displayId`
  AND ad1.`archiveDisplayId` > ad2.`archiveDisplayId`;

ALTER TABLE `ArchiveDisplay` ADD CONSTRAINT `UQ_ARCHIVEDISPLAY_USER_DISPLAY` UNIQUE (`userId`, `displayId`);
