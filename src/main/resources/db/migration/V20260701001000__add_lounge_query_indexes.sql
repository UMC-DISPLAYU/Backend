CREATE INDEX `IDX_LOUNGEPOST_CURSOR`
    ON `LoungePost` (`postStatus`, `deletedAt`, `loungePostId`);

CREATE INDEX `IDX_LOUNGEPOST_CATEGORY_CURSOR`
    ON `LoungePost` (`postStatus`, `deletedAt`, `category`, `loungePostId`);

CREATE INDEX `IDX_LOUNGECOMMENT_ROOT_CURSOR`
    ON `LoungeComment` (`loungePostId`, `parentCommentId`, `commentStatus`, `deletedAt`, `loungeCommentId`);

CREATE INDEX `IDX_LOUNGECOMMENT_REPLY_CURSOR`
    ON `LoungeComment` (`parentCommentId`, `commentStatus`, `deletedAt`, `loungeCommentId`);

CREATE INDEX `IDX_LOUNGEPOSTLIKE_USER_POST`
    ON `LoungePostLike` (`userId`, `loungePostId`);

CREATE INDEX `IDX_LOUNGECOMMENTLIKE_USER_COMMENT`
    ON `LoungeCommentLike` (`userId`, `loungeCommentId`);
