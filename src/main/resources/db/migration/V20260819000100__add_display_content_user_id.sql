ALTER TABLE `DisplayContent`
    ADD COLUMN `userId` BIGINT NULL AFTER `categoryId`;

UPDATE `DisplayContent` dc
    JOIN `DisplayContentCategory` dcc ON dcc.`categoryId` = dc.`categoryId`
    JOIN (
        SELECT `displayId`, MIN(`userId`) AS `teamLeaderUserId`
        FROM `TeamMember`
        WHERE `author` = 'TEAM_LEADER'
          AND `isAccepted` = 1
          AND `deletedAt` IS NULL
        GROUP BY `displayId`
    ) leader ON leader.`displayId` = dcc.`displayId`
SET dc.`userId` = leader.`teamLeaderUserId`
WHERE dc.`userId` IS NULL;

ALTER TABLE `DisplayContent`
    ADD INDEX `IDX_DISPLAYCONTENT_USER` (`userId`),
    ADD CONSTRAINT `FK_DISPLAYCONTENT_USER`
        FOREIGN KEY (`userId`) REFERENCES `User` (`userId`);
