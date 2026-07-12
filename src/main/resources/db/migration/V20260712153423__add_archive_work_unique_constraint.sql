DELETE aw1 FROM `ArchiveWork` aw1
INNER JOIN `ArchiveWork` aw2
  ON aw1.`userId` = aw2.`userId`
  AND aw1.`displayArtworkId` = aw2.`displayArtworkId`
  AND aw1.`archiveWorkId` > aw2.`archiveWorkId`;

ALTER TABLE `ArchiveWork` ADD CONSTRAINT `UQ_ARCHIVEWORK_USER_DISPLAYARTWORK` UNIQUE (`userId`, `displayArtworkId`);
