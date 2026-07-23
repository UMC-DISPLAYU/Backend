ALTER TABLE `DisplayInvitation`
    ADD COLUMN `status` enum ('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'PENDING'
        COMMENT 'PENDING, ACCEPTED, REJECTED' AFTER `userId2`,
    ADD COLUMN `respondedAt` datetime NULL AFTER `createdAt`,
    ADD COLUMN `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `createdAt`;

UPDATE `DisplayInvitation`
SET `status` = 'REJECTED',
    `respondedAt` = `deletedAt`
WHERE `deletedAt` IS NOT NULL;

ALTER TABLE `DisplayInvitation`
    ADD COLUMN `activePendingInviteeUserId` bigint
        GENERATED ALWAYS AS (
            IF((`status` = 'PENDING' AND `deletedAt` IS NULL), `userId2`, NULL)
        ) STORED,
    ADD CONSTRAINT `UQ_DISPLAYINVITATION_PENDING_DISPLAY_INVITEE`
        UNIQUE (`displayId`, `activePendingInviteeUserId`),
    ADD INDEX `IDX_DISPLAYINVITATION_INVITEE_STATUS` (`userId2`, `status`, `disInvitationId`),
    ADD INDEX `IDX_DISPLAYINVITATION_DISPLAY_INVITEE` (`displayId`, `userId2`);

ALTER TABLE `TeamMember`
    ADD CONSTRAINT `UQ_TEAMMEMBER_DISPLAY_USER`
        UNIQUE (`displayId`, `userId`);
