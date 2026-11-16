CREATE TABLE `Notification`
(
    `notificationId`  BIGINT AUTO_INCREMENT PRIMARY KEY,
    `recipientUserId` BIGINT       NOT NULL,
    `type`            VARCHAR(50)  NOT NULL,
    `title`           VARCHAR(100) NOT NULL,
    `body`            TEXT         NOT NULL,
    `targetType`      VARCHAR(30)  NULL,
    `targetId`        BIGINT       NULL,
    `createdAt`       DATETIME(6)  NOT NULL,
    INDEX `IDX_NOTIFICATION_RECIPIENT_ID` (`recipientUserId`, `notificationId`)
);

CREATE TABLE `NotificationReadState`
(
    `userId`     BIGINT      NOT NULL PRIMARY KEY,
    `lastReadAt` DATETIME(6) NOT NULL
);
