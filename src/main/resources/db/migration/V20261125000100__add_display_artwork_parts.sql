CREATE TABLE ArtworkPart (
    displayArtworkId BIGINT NOT NULL,
    partOrder INT NOT NULL,
    artworkName VARCHAR(255) NOT NULL,
    productionYear INT NULL,
    materialMedia VARCHAR(255) NULL,
    size VARCHAR(255) NULL,
    PRIMARY KEY (displayArtworkId, partOrder),
    CONSTRAINT FK_ARTWORKPART_DISPLAYARTWORK FOREIGN KEY (displayArtworkId)
        REFERENCES DisplayArtwork (displayArtworkId)
);

ALTER TABLE ArtworkImage ADD COLUMN partIndex INT NULL;

UPDATE ArtworkImage SET partIndex = 0 WHERE imageType = 'ARTWORK';

-- Older clients could select a process photo explicitly or through the sort-order fallback.
UPDATE DisplayArtwork a
SET artworkCropX = NULL, artworkCropY = NULL, artworkCropW = NULL, artworkCropH = NULL
WHERE a.artworkCropX IS NOT NULL
  AND (SELECT i.imageType FROM ArtworkImage i
       WHERE i.displayArtworkId = a.displayArtworkId
       ORDER BY i.isThumbnail DESC, i.sortOrder, i.artImageId LIMIT 1) = 'WORK_PROCESS';

UPDATE ArtworkImage SET isThumbnail = FALSE WHERE imageType = 'WORK_PROCESS' AND isThumbnail = TRUE;
