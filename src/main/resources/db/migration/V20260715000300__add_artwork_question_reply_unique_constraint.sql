DELETE reply
FROM `ArtworkQuestionReply` reply
JOIN (
    SELECT `artQueId`, MIN(`queReplyId`) AS `keepReplyId`
    FROM `ArtworkQuestionReply`
    GROUP BY `artQueId`
    HAVING COUNT(*) > 1
) duplicated ON reply.`artQueId` = duplicated.`artQueId`
WHERE reply.`queReplyId` <> duplicated.`keepReplyId`;

ALTER TABLE `ArtworkQuestionReply`
    ADD CONSTRAINT `UK_ARTWORKQUESTIONREPLY_ARTQUE` UNIQUE (`artQueId`);
