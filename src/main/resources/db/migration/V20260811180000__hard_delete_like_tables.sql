DELETE FROM `DisplayLike` WHERE `deletedAt` IS NOT NULL;
DELETE FROM `DisplayArtworkLike` WHERE `deletedAt` IS NOT NULL;
DELETE FROM `PersonalArtworkLike` WHERE `deletedAt` IS NOT NULL;
DELETE FROM `ArtworkFeelingLike` WHERE `deletedAt` IS NOT NULL;
DELETE FROM `ArtworkFeelingReplyLike` WHERE `deletedAt` IS NOT NULL;
DELETE FROM `ArtworkQuestionLike` WHERE `deletedAt` IS NOT NULL;
DELETE FROM `ArtworkQuestionReplyLike` WHERE `deletedAt` IS NOT NULL;
DELETE FROM `PersonalArtworkFeelingLike` WHERE `deletedAt` IS NOT NULL;
DELETE FROM `PersonalArtworkFeelingReplyLike` WHERE `deletedAt` IS NOT NULL;
DELETE FROM `PersonalArtworkQuestionLike` WHERE `deletedAt` IS NOT NULL;
DELETE FROM `PersonalArtworkQuestionReplyLike` WHERE `deletedAt` IS NOT NULL;
DELETE FROM `DisplayReviewLike` WHERE `deletedAt` IS NOT NULL;
DELETE FROM `DisplayReviewReplyLike` WHERE `deletedAt` IS NOT NULL;

ALTER TABLE `DisplayReviewLike`
    DROP INDEX `IDX_DISPLAY_REVIEW_LIKE_REVIEW`;
CREATE INDEX `IDX_DISPLAY_REVIEW_LIKE_REVIEW`
    ON `DisplayReviewLike` (`displayReviewId`);

ALTER TABLE `DisplayReviewReplyLike`
    DROP INDEX `IDX_DISPLAY_REVIEW_REPLY_LIKE_REPLY`;
CREATE INDEX `IDX_DISPLAY_REVIEW_REPLY_LIKE_REPLY`
    ON `DisplayReviewReplyLike` (`displayReviewReplyId`);

ALTER TABLE `ArtworkFeelingReplyLike`
    DROP INDEX `IDX_ARTWORKFEELINGREPLYLIKE_REPLY`;
CREATE INDEX `IDX_ARTWORKFEELINGREPLYLIKE_REPLY`
    ON `ArtworkFeelingReplyLike` (`feelingReplyId`);

ALTER TABLE `PersonalArtworkFeelingReplyLike`
    DROP INDEX `IDX_PERSONALARTWORKFEELINGREPLYLIKE_REPLY`;
CREATE INDEX `IDX_PERSONALARTWORKFEELINGREPLYLIKE_REPLY`
    ON `PersonalArtworkFeelingReplyLike` (`personalFeelingReplyId`);

ALTER TABLE `ArtworkQuestionLike`
    DROP INDEX `IDX_ARTWORKQUESTIONLIKE_QUESTION`;
CREATE INDEX `IDX_ARTWORKQUESTIONLIKE_QUESTION`
    ON `ArtworkQuestionLike` (`questionId`);

ALTER TABLE `ArtworkQuestionReplyLike`
    DROP INDEX `IDX_ARTWORKQUESTIONREPLYLIKE_REPLY`;
CREATE INDEX `IDX_ARTWORKQUESTIONREPLYLIKE_REPLY`
    ON `ArtworkQuestionReplyLike` (`questionReplyId`);

ALTER TABLE `PersonalArtworkQuestionLike`
    DROP INDEX `IDX_PERSONALARTWORKQUESTIONLIKE_QUESTION`;
CREATE INDEX `IDX_PERSONALARTWORKQUESTIONLIKE_QUESTION`
    ON `PersonalArtworkQuestionLike` (`personalQuestionId`);

ALTER TABLE `PersonalArtworkQuestionReplyLike`
    DROP INDEX `IDX_PERSONALARTWORKQUESTIONREPLYLIKE_REPLY`;
CREATE INDEX `IDX_PERSONALARTWORKQUESTIONREPLYLIKE_REPLY`
    ON `PersonalArtworkQuestionReplyLike` (`personalQuestionReplyId`);
