-- ArchiveArtist가 Creator(작품별 크레딧 기록)를 잘못 참조하고 있던 것을
-- ArtistProfile(사람 단위 고유 식별자, userId UNIQUE)로 교체한다.
-- ArchiveArtist 기능은 아직 배포된 적이 없어 실제 데이터가 없으므로 컬럼을 안전하게 교체한다.

ALTER TABLE `ArchiveArtist` DROP FOREIGN KEY `FK_ARCHIVEARTIST_CREATOR`;

ALTER TABLE `ArchiveArtist` DROP COLUMN `creatorId`;

ALTER TABLE `ArchiveArtist` ADD COLUMN `artistProfileId` BIGINT NOT NULL;

ALTER TABLE `ArchiveArtist`
  ADD CONSTRAINT `FK_ARCHIVEARTIST_ARTISTPROFILE`
    FOREIGN KEY (`artistProfileId`) REFERENCES `ArtistProfile` (`artistProfileId`);

ALTER TABLE `ArchiveArtist`
  ADD CONSTRAINT `UQ_ARCHIVEARTIST_USER_ARTISTPROFILE` UNIQUE (`userId`, `artistProfileId`);
