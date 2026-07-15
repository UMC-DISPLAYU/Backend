INSERT INTO `ArtworkFeelingLike` (`createdAt`, `updatedAt`, `deletedAt`, `feelingId`, `userId`)
SELECT '2026-07-01 17:50:00', '2026-07-01 17:50:00', NULL, 1, 2
WHERE EXISTS (SELECT 1 FROM `ArtworkFeeling` WHERE `feelingId` = 1 AND `deletedAt` IS NULL)
  AND EXISTS (SELECT 1 FROM `User` WHERE `userId` = 2)
  AND NOT EXISTS (SELECT 1 FROM `ArtworkFeelingLike` WHERE `feelingId` = 1 AND `userId` = 2)
UNION ALL
SELECT '2026-07-01 17:51:00', '2026-07-01 17:51:00', NULL, 1, 3
WHERE EXISTS (SELECT 1 FROM `ArtworkFeeling` WHERE `feelingId` = 1 AND `deletedAt` IS NULL)
  AND EXISTS (SELECT 1 FROM `User` WHERE `userId` = 3)
  AND NOT EXISTS (SELECT 1 FROM `ArtworkFeelingLike` WHERE `feelingId` = 1 AND `userId` = 3)
UNION ALL
SELECT '2026-07-01 17:52:00', '2026-07-01 17:52:00', NULL, 1, 4
WHERE EXISTS (SELECT 1 FROM `ArtworkFeeling` WHERE `feelingId` = 1 AND `deletedAt` IS NULL)
  AND EXISTS (SELECT 1 FROM `User` WHERE `userId` = 4)
  AND NOT EXISTS (SELECT 1 FROM `ArtworkFeelingLike` WHERE `feelingId` = 1 AND `userId` = 4)
UNION ALL
SELECT '2026-07-01 17:53:00', '2026-07-01 17:53:00', NULL, 2, 1
WHERE EXISTS (SELECT 1 FROM `ArtworkFeeling` WHERE `feelingId` = 2 AND `deletedAt` IS NULL)
  AND EXISTS (SELECT 1 FROM `User` WHERE `userId` = 1)
  AND NOT EXISTS (SELECT 1 FROM `ArtworkFeelingLike` WHERE `feelingId` = 2 AND `userId` = 1)
UNION ALL
SELECT '2026-07-01 17:54:00', '2026-07-01 17:54:00', NULL, 2, 3
WHERE EXISTS (SELECT 1 FROM `ArtworkFeeling` WHERE `feelingId` = 2 AND `deletedAt` IS NULL)
  AND EXISTS (SELECT 1 FROM `User` WHERE `userId` = 3)
  AND NOT EXISTS (SELECT 1 FROM `ArtworkFeelingLike` WHERE `feelingId` = 2 AND `userId` = 3)
UNION ALL
SELECT '2026-07-01 17:55:00', '2026-07-01 17:55:00', NULL, 3, 1
WHERE EXISTS (SELECT 1 FROM `ArtworkFeeling` WHERE `feelingId` = 3 AND `deletedAt` IS NULL)
  AND EXISTS (SELECT 1 FROM `User` WHERE `userId` = 1)
  AND NOT EXISTS (SELECT 1 FROM `ArtworkFeelingLike` WHERE `feelingId` = 3 AND `userId` = 1)
UNION ALL
SELECT '2026-07-01 17:56:00', '2026-07-01 17:56:00', NULL, 4, 5
WHERE EXISTS (SELECT 1 FROM `ArtworkFeeling` WHERE `feelingId` = 4 AND `deletedAt` IS NULL)
  AND EXISTS (SELECT 1 FROM `User` WHERE `userId` = 5)
  AND NOT EXISTS (SELECT 1 FROM `ArtworkFeelingLike` WHERE `feelingId` = 4 AND `userId` = 5)
UNION ALL
SELECT '2026-07-01 17:57:00', '2026-07-01 17:57:00', NULL, 5, 6
WHERE EXISTS (SELECT 1 FROM `ArtworkFeeling` WHERE `feelingId` = 5 AND `deletedAt` IS NULL)
  AND EXISTS (SELECT 1 FROM `User` WHERE `userId` = 6)
  AND NOT EXISTS (SELECT 1 FROM `ArtworkFeelingLike` WHERE `feelingId` = 5 AND `userId` = 6)
UNION ALL
SELECT '2026-07-01 17:58:00', '2026-07-01 17:58:00', NULL, 6, 7
WHERE EXISTS (SELECT 1 FROM `ArtworkFeeling` WHERE `feelingId` = 6 AND `deletedAt` IS NULL)
  AND EXISTS (SELECT 1 FROM `User` WHERE `userId` = 7)
  AND NOT EXISTS (SELECT 1 FROM `ArtworkFeelingLike` WHERE `feelingId` = 6 AND `userId` = 7)
UNION ALL
SELECT '2026-07-01 17:59:00', '2026-07-01 18:10:00', '2026-07-01 18:10:00', 7, 8
WHERE EXISTS (SELECT 1 FROM `ArtworkFeeling` WHERE `feelingId` = 7 AND `deletedAt` IS NULL)
  AND EXISTS (SELECT 1 FROM `User` WHERE `userId` = 8)
  AND NOT EXISTS (SELECT 1 FROM `ArtworkFeelingLike` WHERE `feelingId` = 7 AND `userId` = 8);
