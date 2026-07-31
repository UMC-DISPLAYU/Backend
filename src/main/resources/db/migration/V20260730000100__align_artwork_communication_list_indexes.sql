CREATE INDEX `IDX_ARTWORKFEELING_LIST`
    ON `ArtworkFeeling` (`displayArtworkId`, `deletedAt`, `feelingId`);

CREATE INDEX `IDX_ARTWORKFEELINGREPLY_LIST`
    ON `ArtworkFeelingReply` (`feelingId`, `deletedAt`, `feelingReplyId`);

CREATE INDEX `IDX_ARTWORKQUESTION_LIST`
    ON `ArtworkQuestion` (`displayArtworkId`, `deletedAt`, `questionId`);

CREATE INDEX `IDX_PERSONALARTWORKFEELINGREPLY_LIST_NEW`
    ON `PersonalArtworkFeelingReply`
        (`personalFeelingId`, `deletedAt`, `personalFeelingReplyId`);

DROP INDEX `IDX_PERSONALARTWORKFEELINGREPLY_LIST`
    ON `PersonalArtworkFeelingReply`;

ALTER TABLE `PersonalArtworkFeelingReply`
    RENAME INDEX `IDX_PERSONALARTWORKFEELINGREPLY_LIST_NEW`
        TO `IDX_PERSONALARTWORKFEELINGREPLY_LIST`;
