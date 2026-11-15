ALTER TABLE `User`
    ADD COLUMN `accountStatus` ENUM ('ACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE' AFTER `deletedAt`,
    ADD COLUMN `suspendedAt` DATETIME NULL AFTER `accountStatus`,
    ADD COLUMN `suspensionOperatorId` BIGINT NULL AFTER `suspendedAt`,
    ADD COLUMN `suspensionReason` VARCHAR(1000) NULL AFTER `suspensionOperatorId`;

CREATE INDEX `IDX_USER_ACCOUNT_STATUS` ON `User` (`accountStatus`);
