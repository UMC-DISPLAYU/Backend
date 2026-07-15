ALTER TABLE `ArtworkQuestionReply`
    ADD COLUMN `creatorId` BIGINT NULL AFTER `artQueId`;

UPDATE `ArtworkQuestionReply` reply
JOIN `ArtworkQuestion` question ON reply.`artQueId` = question.`artQueId`
JOIN (
    SELECT `displayArtworkId`, MIN(`creatorId`) AS `creatorId`
    FROM `Creator`
    WHERE `isContact` = TRUE
    GROUP BY `displayArtworkId`
) contact_creator ON contact_creator.`displayArtworkId` = question.`displayArtworkId`
SET reply.`creatorId` = contact_creator.`creatorId`
WHERE reply.`creatorId` IS NULL;
