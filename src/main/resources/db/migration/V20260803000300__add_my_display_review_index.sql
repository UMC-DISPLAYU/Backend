CREATE INDEX `IDX_DISPLAY_REVIEW_MY_LIST`
    ON `DisplayReview` (`userId`, `deletedAt`, `displayReviewId`);
