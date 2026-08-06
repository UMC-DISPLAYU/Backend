ALTER TABLE `ArtistProfile`
    ADD COLUMN `profileImageUrl` VARCHAR(2048) NULL AFTER `artistName`;

UPDATE `ArtistProfile` AS artistProfile
JOIN `User` AS user ON user.`userId` = artistProfile.`userId`
SET artistProfile.`profileImageUrl` = user.`profileImageUrl`;
