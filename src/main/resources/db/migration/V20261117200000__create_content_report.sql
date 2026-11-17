CREATE TABLE ContentReport (
    reportId BIGINT NOT NULL AUTO_INCREMENT,
    targetType VARCHAR(30) NOT NULL,
    targetId BIGINT NOT NULL,
    reporterUserId BIGINT NOT NULL,
    reportedUserId BIGINT NOT NULL,
    reason VARCHAR(50) NOT NULL,
    createdAt DATETIME(6) NOT NULL,
    PRIMARY KEY (reportId),
    CONSTRAINT UQ_CONTENT_REPORT_REPORTER_TARGET UNIQUE (reporterUserId, targetType, targetId)
);
