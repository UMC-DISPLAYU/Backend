-- Verified from production snapshot du-snapshot (2026-08-11 15:18 KST).
-- Pre-migration targets: User.nicknameChangeAt=7, User.deletedAt=1,
-- DisplayInvitation.respondedAt=6. The exact PK/value pairs below are the source markers;
-- after migration the same predicates match 0 rows, so reruns cannot subtract twice.
-- Samples: User 32 nicknameChangeAt 2026-08-09 22:59:40 -> 13:59:40,
-- DisplayInvitation 21 respondedAt 2026-08-11 12:13:39 -> 03:13:39.

UPDATE `User`
SET `nicknameChangeAt` = DATE_SUB(`nicknameChangeAt`, INTERVAL 9 HOUR)
WHERE (`userId` = 38 AND `nicknameChangeAt` = '2026-08-10 01:03:32')
   OR (`userId` = 33 AND `nicknameChangeAt` = '2026-08-09 22:45:54')
   OR (`userId` = 32 AND `nicknameChangeAt` = '2026-08-09 22:59:40')
   OR (`userId` = 31 AND `nicknameChangeAt` = '2026-08-08 02:00:31')
   OR (`userId` = 15 AND `nicknameChangeAt` = '2026-08-06 20:01:46')
   OR (`userId` = 13 AND `nicknameChangeAt` = '2026-07-31 08:48:12')
   OR (`userId` = 11 AND `nicknameChangeAt` = '2026-07-27 14:25:37');

UPDATE `User`
SET `deletedAt` = DATE_SUB(`deletedAt`, INTERVAL 9 HOUR)
WHERE `userId` = 32
  AND `deletedAt` = '2026-08-10 00:08:49';

UPDATE `DisplayInvitation`
SET `respondedAt` = DATE_SUB(`respondedAt`, INTERVAL 9 HOUR)
WHERE (`disInvitationId` = 21 AND `respondedAt` = '2026-08-11 12:13:39')
   OR (`disInvitationId` = 20 AND `respondedAt` = '2026-08-11 12:08:22')
   OR (`disInvitationId` = 19 AND `respondedAt` = '2026-08-11 12:06:57')
   OR (`disInvitationId` = 13 AND `respondedAt` = '2026-08-08 17:43:41')
   OR (`disInvitationId` = 12 AND `respondedAt` = '2026-08-06 20:56:07')
   OR (`disInvitationId` = 11 AND `respondedAt` = '2026-08-06 20:05:53');
