DELETE aa1 FROM `ArchiveArtist` aa1
INNER JOIN `ArchiveArtist` aa2
  ON aa1.`userId` = aa2.`userId`
  AND aa1.`creatorId` = aa2.`creatorId`
  AND (
    aa1.`savedAt` < aa2.`savedAt`
    OR (
      aa1.`savedAt` = aa2.`savedAt`
      AND aa1.`archiveArtistId` < aa2.`archiveArtistId`
    )
  );

ALTER TABLE `ArchiveArtist` ADD CONSTRAINT `UQ_ARCHIVEARTIST_USER_CREATOR` UNIQUE (`userId`, `creatorId`);
