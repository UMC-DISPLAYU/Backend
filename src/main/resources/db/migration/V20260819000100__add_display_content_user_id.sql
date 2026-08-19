ALTER TABLE `DisplayContent`
    ADD COLUMN `userId` BIGINT NULL AFTER `categoryId`;

UPDATE `DisplayContent` dc
    JOIN `DisplayContentCategory` dcc ON dcc.`categoryId` = dc.`categoryId`
    JOIN (
        SELECT tm.`displayId`, MIN(tm.`userId`) AS `teamLeaderUserId`
        FROM `TeamMember` tm
            JOIN `User` u ON u.`userId` = tm.`userId`
        WHERE tm.`author` = 'TEAM_LEADER'
          AND tm.`isAccepted` = 1
          AND tm.`deletedAt` IS NULL
        GROUP BY tm.`displayId`
    ) leader ON leader.`displayId` = dcc.`displayId`
SET dc.`userId` = leader.`teamLeaderUserId`
WHERE dc.`userId` IS NULL;

ALTER TABLE `DisplayContent`
    ADD INDEX `IDX_DISPLAYCONTENT_USER` (`userId`),
    ADD CONSTRAINT `FK_DISPLAYCONTENT_USER`
        FOREIGN KEY (`userId`) REFERENCES `User` (`userId`);
