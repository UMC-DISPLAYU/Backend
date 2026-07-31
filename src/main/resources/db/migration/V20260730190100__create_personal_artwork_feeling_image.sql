CREATE TABLE PersonalArtworkFeelingImage
(
    personalFeelingImageId BIGINT AUTO_INCREMENT PRIMARY KEY,
    imageUrl               TEXT                               NOT NULL,
    width                  INT                                NOT NULL,
    height                 INT                                NOT NULL,
    sortOrder              INT                                NOT NULL,
    createdAt              DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updatedAt              DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    personalFeelingId      BIGINT                             NOT NULL,
    CONSTRAINT FK_PERSONAL_ARTWORK_FEELING_IMAGE_FEELING
        FOREIGN KEY (personalFeelingId)
            REFERENCES PersonalArtworkFeeling (personalFeelingId)
);

CREATE INDEX IDX_PERSONAL_ARTWORK_FEELING_IMAGE_FEELING_SORT
    ON PersonalArtworkFeelingImage (personalFeelingId, sortOrder);
