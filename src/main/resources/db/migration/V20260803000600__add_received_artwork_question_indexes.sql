CREATE INDEX `IDX_CREATOR_RECEIVED_QUESTION`
    ON `Creator` (`userId`, `isContact`, `displayArtworkId`);

CREATE INDEX `IDX_ARTWORK_QUESTION_RECEIVED_LIST`
    ON `ArtworkQuestion` (`displayArtworkId`, `answerStatus`, `deletedAt`, `createdAt`, `questionId`);

CREATE INDEX `IDX_PERSONAL_ARTWORK_QUESTION_RECEIVED_LIST`
    ON `PersonalArtworkQuestion` (`personalArtworkId`, `answerStatus`, `deletedAt`, `createdAt`, `personalQuestionId`);
