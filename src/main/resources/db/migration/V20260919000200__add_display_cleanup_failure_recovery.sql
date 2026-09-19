ALTER TABLE `DisplayDeletionCleanupFailure`
    ADD COLUMN `recoveredAt` DATETIME(6) NULL;

CREATE INDEX `idx_display_cleanup_failure_recovered_id`
    ON `DisplayDeletionCleanupFailure` (`recoveredAt`, `displayDeletionCleanupFailureId`);
