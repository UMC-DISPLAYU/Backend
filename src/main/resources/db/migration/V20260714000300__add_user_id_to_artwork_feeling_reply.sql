ALTER TABLE `ArtworkFeelingReply`
    ADD COLUMN `userId` BIGINT NULL AFTER `feelingId`;

UPDATE `ArtworkFeelingReply`
SET `userId` = 1
WHERE `userId` IS NULL;

ALTER TABLE `ArtworkFeelingReply`
    MODIFY COLUMN `userId` BIGINT NOT NULL;
