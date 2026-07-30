CREATE INDEX `IDX_ARTWORKFEELING_LIST`
    ON `ArtworkFeeling` (`displayArtworkId`, `deletedAt`, `feelingId`);

CREATE INDEX `IDX_ARTWORKFEELINGREPLY_LIST`
    ON `ArtworkFeelingReply` (`feelingId`, `deletedAt`, `feelingReplyId`);

CREATE INDEX `IDX_ARTWORKQUESTION_LIST`
    ON `ArtworkQuestion` (`displayArtworkId`, `deletedAt`, `artQueId`);

DROP INDEX `IDX_PERSONALARTWORKFEELINGREPLY_LIST`
    ON `PersonalArtworkFeelingReply`;

CREATE INDEX `IDX_PERSONALARTWORKFEELINGREPLY_LIST`
    ON `PersonalArtworkFeelingReply`
        (`personalFeelingId`, `deletedAt`, `personalFeelingReplyId`);
