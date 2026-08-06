ALTER TABLE `TeamMember`
    ADD COLUMN `deletedAt` datetime NULL AFTER `isAccepted`;

ALTER TABLE `TeamMember`
    DROP INDEX `UQ_TEAMMEMBER_DISPLAY_USER`;

ALTER TABLE `TeamMember`
    ADD COLUMN `activeUserId` bigint
        GENERATED ALWAYS AS (
            IF(`deletedAt` IS NULL, `userId`, NULL)
        ) STORED,
    ADD CONSTRAINT `UQ_TEAMMEMBER_ACTIVE_DISPLAY_USER`
        UNIQUE (`displayId`, `activeUserId`);
