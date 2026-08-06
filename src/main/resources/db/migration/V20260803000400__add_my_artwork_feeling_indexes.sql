CREATE INDEX `IDX_ARTWORK_FEELING_MY_LIST`
    ON `ArtworkFeeling` (`userId`, `deletedAt`, `createdAt`, `feelingId`);

CREATE INDEX `IDX_PERSONAL_ARTWORK_FEELING_MY_LIST`
    ON `PersonalArtworkFeeling` (`userId`, `deletedAt`, `createdAt`, `personalFeelingId`);
