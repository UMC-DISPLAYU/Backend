ALTER TABLE `DisplayReview`
    DROP INDEX `UQ_DISPLAY_REVIEW_DISPLAY_USER`,
    ADD COLUMN `activeReviewUserId` BIGINT
        GENERATED ALWAYS AS (
            CASE WHEN `deletedAt` IS NULL THEN `userId` ELSE NULL END
        ) STORED,
    ADD CONSTRAINT `UQ_DISPLAY_REVIEW_ACTIVE_USER`
        UNIQUE (`displayId`, `activeReviewUserId`);
