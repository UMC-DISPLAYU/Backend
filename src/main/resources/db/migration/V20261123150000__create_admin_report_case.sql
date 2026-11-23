CREATE TABLE `AdminReportCase`
(
    `reportCaseId`   BIGINT        NOT NULL PRIMARY KEY,
    `commentType`    VARCHAR(30)   NOT NULL,
    `commentId`      BIGINT        NOT NULL,
    `lastReportId`   BIGINT        NOT NULL,
    `reportCount`    BIGINT        NOT NULL,
    `lastReportedAt` DATETIME(6)   NOT NULL,
    `action`         VARCHAR(30)   NOT NULL,
    `reason`         VARCHAR(1000) NOT NULL,
    `operatorId`     BIGINT        NOT NULL,
    `processedAt`    DATETIME(6)   NOT NULL,
    INDEX `IDX_ADMIN_REPORT_CASE_COMMENT` (`commentType`, `commentId`, `lastReportId`)
);
