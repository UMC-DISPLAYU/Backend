-- 1. NULL 허용으로 컬럼만 먼저 추가 (배포 DB에 이미 존재하는 기존 행과 충돌하지 않도록)
ALTER TABLE `DisplayArtwork`
    ADD COLUMN `registeredByUserId` BIGINT NULL;

-- 2. 기존 행 백필: 등록자 정보가 없는 과거 데이터는 해당 전시를 만든 사람(대표자)을 등록자로 간주
UPDATE `DisplayArtwork` da
    JOIN `Display` d ON da.`displayId` = d.`displayId`
SET da.`registeredByUserId` = d.`userId`
WHERE da.`registeredByUserId` IS NULL;

-- 3. 백필 완료 후 NOT NULL로 조임
ALTER TABLE `DisplayArtwork`
    MODIFY COLUMN `registeredByUserId` BIGINT NOT NULL;

-- 4. 모든 값이 실존하는 유저를 가리키는 것이 보장된 뒤 FK 추가
ALTER TABLE `DisplayArtwork`
    ADD CONSTRAINT `FK_DISPLAYARTWORK_REGISTEREDBY` FOREIGN KEY (`registeredByUserId`) REFERENCES `User` (`userId`);
