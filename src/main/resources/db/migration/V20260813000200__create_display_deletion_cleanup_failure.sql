CREATE TABLE `DisplayDeletionCleanupFailure` (
    `displayDeletionCleanupFailureId` BIGINT NOT NULL AUTO_INCREMENT,
    `displayId` BIGINT NOT NULL,
    `deletedAt` DATETIME NOT NULL,
    `retryCount` INT NOT NULL,
    `exceptionType` VARCHAR(255) NOT NULL,
    `failureMessage` VARCHAR(1000) NULL,
    `createdAt` DATETIME NOT NULL,
    `updatedAt` DATETIME NOT NULL,
    PRIMARY KEY (`displayDeletionCleanupFailureId`)
);

CREATE INDEX `IDX_DISPLAY_DELETION_CLEANUP_FAILURE_DISPLAY`
    ON `DisplayDeletionCleanupFailure` (`displayId`, `createdAt`);
