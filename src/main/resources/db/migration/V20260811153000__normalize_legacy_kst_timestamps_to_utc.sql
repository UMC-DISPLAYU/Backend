-- Nickname changes were written with the Asia/Seoul Clock. Keep initial values that match createdAt.
UPDATE `User`
SET `nicknameChangeAt` = DATE_SUB(`nicknameChangeAt`, INTERVAL 9 HOUR)
WHERE `nicknameChangeAt` IS NOT NULL
  AND `nicknameChangeAt` <> `createdAt`;

-- Application withdrawals updated updatedAt in UTC and deletedAt in KST in the same transaction.
UPDATE `User`
SET `deletedAt` = DATE_SUB(`deletedAt`, INTERVAL 9 HOUR)
WHERE `deletedAt` = DATE_ADD(`updatedAt`, INTERVAL 9 HOUR);

-- Current invitation responses used the KST Clock. Rows with deletedAt are legacy migration data.
UPDATE `DisplayInvitation`
SET `respondedAt` = DATE_SUB(`respondedAt`, INTERVAL 9 HOUR)
WHERE `respondedAt` IS NOT NULL
  AND `deletedAt` IS NULL;
