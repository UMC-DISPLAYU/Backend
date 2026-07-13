CREATE TABLE `DisplayField` (
                                `displayFieldId` BIGINT NOT NULL AUTO_INCREMENT,
                                `field` ENUM('PAINTING', 'DESIGN', 'PHOTOGRAPHY', 'ARCHITECTURE', 'VIDEO', 'CRAFTS', 'SCULPTURE', 'FASHION', 'INTERDISCIPLINARY', 'OTHERS') NOT NULL,
                                `sortOrder` INT NOT NULL,
                                `displayId` BIGINT NOT NULL,
                                CONSTRAINT `PK_DISPLAYFIELD` PRIMARY KEY (`displayFieldId`),
                                CONSTRAINT `UK_DISPLAYFIELD_DISPLAY_FIELD` UNIQUE (`displayId`, `field`),
                                CONSTRAINT `FK_DISPLAYFIELD_DISPLAY` FOREIGN KEY (`displayId`) REFERENCES `Display` (`displayId`)
);

INSERT INTO `DisplayField` (`field`, `sortOrder`, `displayId`)
SELECT `displayField`, 0, `displayId`
FROM `Display`;

ALTER TABLE `Display`
    DROP COLUMN `displayField`;
