CREATE TABLE `SchoolEmailVerification` (
    `verificationId` BIGINT NOT NULL AUTO_INCREMENT,
    `schoolEmail` VARCHAR(255) NOT NULL,
    `verificationCode` VARCHAR(10) NOT NULL,
    `expiresAt` DATETIME NOT NULL,
    `createdAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (`verificationId`)
);