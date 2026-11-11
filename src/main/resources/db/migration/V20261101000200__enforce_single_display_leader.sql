ALTER TABLE `TeamMember`
    ADD COLUMN `activeAcceptedLeaderDisplayId` bigint
        GENERATED ALWAYS AS (
            IF((`author` = 'TEAM_LEADER' AND `isAccepted` = 1 AND `deletedAt` IS NULL), `displayId`, NULL)
        ) STORED,
    ADD CONSTRAINT `UQ_TEAMMEMBER_ACTIVE_ACCEPTED_LEADER`
        UNIQUE (`activeAcceptedLeaderDisplayId`);
