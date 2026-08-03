CREATE INDEX `IDX_ARTWORK_QUESTION_MY_LIST`
    ON `ArtworkQuestion` (`userId`, `deletedAt`, `createdAt`, `questionId`);

CREATE INDEX `IDX_PERSONAL_ARTWORK_QUESTION_MY_LIST`
    ON `PersonalArtworkQuestion` (`userId`, `deletedAt`, `createdAt`, `personalQuestionId`);
