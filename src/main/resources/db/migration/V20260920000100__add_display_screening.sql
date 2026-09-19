ALTER TABLE `Display`
    MODIFY COLUMN `status`
        ENUM ('DRAFT', 'PENDING_REVIEW', 'REJECTED', 'PUBLISHED')
        NOT NULL DEFAULT 'DRAFT'
        COMMENT 'DRAFT, PENDING_REVIEW, REJECTED, PUBLISHED';

CREATE TABLE `DisplayScreening`
(
    `screeningId`     BIGINT AUTO_INCREMENT PRIMARY KEY,
    `displayId`       BIGINT                                          NOT NULL,
    `requesterId`     BIGINT                                          NOT NULL,
    `status`          ENUM ('PENDING_REVIEW', 'REJECTED', 'PUBLISHED') NOT NULL,
    `requestedAt`     DATETIME                                        NOT NULL,
    `reviewerId`      BIGINT                                          NULL,
    `processedAt`     DATETIME                                        NULL,
    `rejectionReason` VARCHAR(1000)                                   NULL,
    CONSTRAINT `FK_DISPLAY_SCREENING_DISPLAY`
        FOREIGN KEY (`displayId`) REFERENCES `Display` (`displayId`),
    CONSTRAINT `FK_DISPLAY_SCREENING_REQUESTER`
        FOREIGN KEY (`requesterId`) REFERENCES `User` (`userId`),
    CONSTRAINT `FK_DISPLAY_SCREENING_REVIEWER`
        FOREIGN KEY (`reviewerId`) REFERENCES `User` (`userId`),
    INDEX `IDX_DISPLAY_SCREENING_STATUS_DISPLAY` (`status`, `displayId`),
    INDEX `IDX_DISPLAY_SCREENING_DISPLAY_ID` (`displayId`, `screeningId`)
);
