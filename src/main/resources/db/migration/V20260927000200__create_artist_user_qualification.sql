ALTER TABLE `User`
    ADD COLUMN `qualificationRevision` BIGINT NOT NULL DEFAULT 0;

CREATE TABLE `ArtistUserQualification`
(
  `userId`              BIGINT NOT NULL,
  `schoolEmail`         VARCHAR(255) NULL,
  `universityName`      VARCHAR(255) NULL,
  `schoolVerified`      TINYINT(1) NOT NULL DEFAULT 0,
  `withdrawn`           TINYINT(1) NOT NULL DEFAULT 0,
  `sourceRevision`      BIGINT NOT NULL DEFAULT 0,
  `lastEventId`         VARCHAR(36) NULL,
  `updatedAt`           DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `persistenceVersion`  BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`userId`),
  INDEX `IDX_ARTIST_USER_QUALIFICATION_REVISION` (`sourceRevision`)
);

INSERT INTO `ArtistUserQualification` (
  `userId`,
  `schoolEmail`,
  `universityName`,
  `schoolVerified`,
  `withdrawn`,
  `sourceRevision`,
  `lastEventId`,
  `updatedAt`
)
SELECT
  u.`userId`,
  u.`schoolEmail`,
  u.`univName`,
  CASE
    WHEN NULLIF(TRIM(u.`schoolEmail`), '') IS NOT NULL
      AND NULLIF(TRIM(u.`univName`), '') IS NOT NULL THEN 1
    ELSE 0
  END,
  CASE WHEN u.`deletedAt` IS NOT NULL THEN 1 ELSE 0 END,
  u.`qualificationRevision`,
  NULL,
  COALESCE(u.`updatedAt`, CURRENT_TIMESTAMP(6))
FROM `User` u
ON DUPLICATE KEY UPDATE
  `schoolEmail` = IF(
    VALUES(`sourceRevision`) >= `ArtistUserQualification`.`sourceRevision`,
    VALUES(`schoolEmail`),
    `ArtistUserQualification`.`schoolEmail`
  ),
  `universityName` = IF(
    VALUES(`sourceRevision`) >= `ArtistUserQualification`.`sourceRevision`,
    VALUES(`universityName`),
    `ArtistUserQualification`.`universityName`
  ),
  `schoolVerified` = IF(
    VALUES(`sourceRevision`) >= `ArtistUserQualification`.`sourceRevision`,
    VALUES(`schoolVerified`),
    `ArtistUserQualification`.`schoolVerified`
  ),
  `withdrawn` = IF(
    VALUES(`sourceRevision`) >= `ArtistUserQualification`.`sourceRevision`,
    VALUES(`withdrawn`),
    `ArtistUserQualification`.`withdrawn`
  ),
  `sourceRevision` = GREATEST(
    VALUES(`sourceRevision`),
    `ArtistUserQualification`.`sourceRevision`
  ),
  `updatedAt` = IF(
    VALUES(`sourceRevision`) >= `ArtistUserQualification`.`sourceRevision`,
    VALUES(`updatedAt`),
    `ArtistUserQualification`.`updatedAt`
  );
