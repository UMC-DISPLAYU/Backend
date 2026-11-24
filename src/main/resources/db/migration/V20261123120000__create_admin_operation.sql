CREATE TABLE `AdminOperation`
(
    `operationId` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `targetType`  VARCHAR(40)   NOT NULL,
    `targetId`    BIGINT        NOT NULL,
    `action`      VARCHAR(40)   NOT NULL,
    `operatorId`  BIGINT        NOT NULL,
    `reason`      VARCHAR(1000) NOT NULL,
    `processedAt` DATETIME(6)   NOT NULL,
    INDEX `IDX_ADMIN_OPERATION_TARGET` (`targetType`, `targetId`, `operationId`),
    INDEX `IDX_ADMIN_OPERATION_OPERATOR` (`operatorId`, `operationId`)
);
