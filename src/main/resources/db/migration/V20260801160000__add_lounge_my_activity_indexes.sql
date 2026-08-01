CREATE INDEX `IDX_LOUNGEPOST_USER_CURSOR`
    ON `LoungePost` (`userId`, `postStatus`, `deletedAt`, `loungePostId`);

CREATE INDEX `IDX_LOUNGECOMMENT_USER_CURSOR`
    ON `LoungeComment` (`userId`, `commentStatus`, `deletedAt`, `loungeCommentId`);

CREATE INDEX `IDX_LOUNGEPOSTSCRAP_USER_CURSOR`
    ON `LoungePostScrap` (`userId`, `loungePostScrapId`);
