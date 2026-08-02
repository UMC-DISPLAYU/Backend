CREATE TABLE ArtworkFeelingImage
(
    feelingImageId BIGINT AUTO_INCREMENT PRIMARY KEY,
    imageUrl       TEXT                               NOT NULL,
    width          INT                                NOT NULL,
    height         INT                                NOT NULL,
    sortOrder      INT                                NOT NULL,
    createdAt      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updatedAt      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    feelingId      BIGINT                             NOT NULL,
    CONSTRAINT FK_ARTWORK_FEELING_IMAGE_FEELING
        FOREIGN KEY (feelingId) REFERENCES ArtworkFeeling (feelingId)
);

CREATE INDEX IDX_ARTWORK_FEELING_IMAGE_FEELING_SORT
    ON ArtworkFeelingImage (feelingId, sortOrder);
