ALTER TABLE `User`
    ADD COLUMN `universityId` BIGINT NULL AFTER `univName`;

CREATE INDEX `IDX_USER_UNIVERSITY` ON `User` (`universityId`);

ALTER TABLE `User`
    ADD CONSTRAINT `FK_USER_UNIVERSITY`
    FOREIGN KEY (`universityId`) REFERENCES `University` (`universityId`);

UPDATE `User` u
SET u.`universityId` = COALESCE(
        (SELECT university.`universityId`
         FROM `University` university
         WHERE university.`name` = u.`univName`),
        (SELECT alias.`universityId`
         FROM `UniversityAlias` alias
         WHERE alias.`aliasName` = u.`univName`)
    )
WHERE u.`universityId` IS NULL
  AND u.`univName` IS NOT NULL;

ALTER TABLE `SchoolEmailVerification`
    ADD COLUMN `universityId` BIGINT NULL AFTER `univName`;

CREATE INDEX `IDX_SCHOOL_EMAIL_VERIFICATION_UNIVERSITY`
    ON `SchoolEmailVerification` (`universityId`);

ALTER TABLE `SchoolEmailVerification`
    ADD CONSTRAINT `FK_SCHOOL_EMAIL_VERIFICATION_UNIVERSITY`
    FOREIGN KEY (`universityId`) REFERENCES `University` (`universityId`);

UPDATE `SchoolEmailVerification` verification
SET verification.`universityId` = COALESCE(
        (SELECT university.`universityId`
         FROM `University` university
         WHERE university.`name` = verification.`univName`),
        (SELECT alias.`universityId`
         FROM `UniversityAlias` alias
         WHERE alias.`aliasName` = verification.`univName`)
    )
WHERE verification.`universityId` IS NULL;
