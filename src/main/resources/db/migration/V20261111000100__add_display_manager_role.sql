ALTER TABLE `TeamMember`
    MODIFY COLUMN `author` enum ('TEAM_MEM', 'MANAGER', 'TEAM_LEADER')
        NOT NULL DEFAULT 'TEAM_MEM'
        COMMENT 'TEAM_MEM, MANAGER, TEAM_LEADER';

ALTER TABLE `DisplayInvitation`
    ADD COLUMN `role` enum ('TEAM_MEM', 'MANAGER')
        NOT NULL DEFAULT 'TEAM_MEM'
        COMMENT '초대 수락 후 부여할 역할' AFTER `userId2`;
