-- 작품 분야를 최대 2개까지 지정할 수 있도록 분야를 별도 테이블로 분리한다.
-- 작가 프로필의 활동 분야(AreaOfActivity)와 같은 구조다.
--
-- DisplayArtwork.type / PersonalArtwork.type 컬럼은 남겨둔다. 여러 조회와 응답이 아직 사용하고 있고,
-- 애플리케이션이 첫 번째 분야를 항상 이 컬럼에 함께 반영해 두 값이 어긋나지 않게 유지한다.
-- 컬럼 제거는 프론트가 types로 옮긴 뒤 별도로 진행한다.

CREATE TABLE `ArtworkField`
(
    `artworkFieldId`   BIGINT AUTO_INCREMENT PRIMARY KEY,
    `displayArtworkId` BIGINT      NOT NULL,
    `field`            VARCHAR(50) NOT NULL,
    CONSTRAINT `UQ_ARTWORKFIELD_ARTWORK_FIELD`
        UNIQUE (`displayArtworkId`, `field`),
    CONSTRAINT `FK_ARTWORKFIELD_DISPLAYARTWORK`
        FOREIGN KEY (`displayArtworkId`) REFERENCES `DisplayArtwork` (`displayArtworkId`)
);

CREATE TABLE `PersonalArtworkField`
(
    `personalArtworkFieldId` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `personalArtworkId`      BIGINT      NOT NULL,
    `field`                  VARCHAR(50) NOT NULL,
    CONSTRAINT `UQ_PERSONALARTWORKFIELD_ARTWORK_FIELD`
        UNIQUE (`personalArtworkId`, `field`),
    CONSTRAINT `FK_PERSONALARTWORKFIELD_PERSONALARTWORK`
        FOREIGN KEY (`personalArtworkId`) REFERENCES `PersonalArtwork` (`personalArtworkId`)
);

-- 기존 작품은 분야가 하나씩 있으므로 그대로 옮긴다. 삭제된 작품도 복구 시 분야가 남아 있어야 하므로 함께 채운다.
INSERT INTO `ArtworkField` (`displayArtworkId`, `field`)
SELECT `displayArtworkId`, `type`
FROM `DisplayArtwork`;

INSERT INTO `PersonalArtworkField` (`personalArtworkId`, `field`)
SELECT `personalArtworkId`, `type`
FROM `PersonalArtwork`;
