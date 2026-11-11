CREATE TABLE `University`
(
    `universityId` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name`         VARCHAR(100)                        NOT NULL,
    `active`       TINYINT(1) DEFAULT 1                NOT NULL,
    `createdAt`    DATETIME   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updatedAt`    DATETIME   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT `UQ_UNIVERSITY_NAME` UNIQUE (`name`),
    INDEX `IDX_UNIVERSITY_ACTIVE_NAME` (`active`, `name`)
);

CREATE TABLE `UniversityAlias`
(
    `aliasId`      BIGINT AUTO_INCREMENT PRIMARY KEY,
    `universityId` BIGINT       NOT NULL,
    `aliasName`    VARCHAR(100) NOT NULL,
    CONSTRAINT `UQ_UNIVERSITY_ALIAS_NAME` UNIQUE (`aliasName`),
    CONSTRAINT `FK_UNIVERSITY_ALIAS_UNIVERSITY`
        FOREIGN KEY (`universityId`) REFERENCES `University` (`universityId`),
    INDEX `IDX_UNIVERSITY_ALIAS_UNIVERSITY` (`universityId`)
);

CREATE TABLE `UniversityEmailDomain`
(
    `domainId`     BIGINT AUTO_INCREMENT PRIMARY KEY,
    `universityId` BIGINT       NOT NULL,
    `domain`       VARCHAR(253) NOT NULL,
    CONSTRAINT `UQ_UNIVERSITY_EMAIL_DOMAIN` UNIQUE (`universityId`, `domain`),
    CONSTRAINT `FK_UNIVERSITY_EMAIL_DOMAIN_UNIVERSITY`
        FOREIGN KEY (`universityId`) REFERENCES `University` (`universityId`),
    INDEX `IDX_UNIVERSITY_EMAIL_DOMAIN_LOOKUP` (`domain`, `universityId`)
);
