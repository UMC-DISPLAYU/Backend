CREATE TEMPORARY TABLE TMP_DISPLAYIMAGE_ACTIVE_UNIQUE_GUARD
(
    displayId  BIGINT       NOT NULL,
    imageType  VARCHAR(255) NOT NULL,
    sortOrder  INT          NOT NULL,
    PRIMARY KEY (displayId, imageType, sortOrder)
);

INSERT INTO TMP_DISPLAYIMAGE_ACTIVE_UNIQUE_GUARD (displayId, imageType, sortOrder)
SELECT displayId, imageType, sortOrder
FROM DisplayImage
WHERE deletedAt IS NULL;

DROP TEMPORARY TABLE TMP_DISPLAYIMAGE_ACTIVE_UNIQUE_GUARD;

ALTER TABLE DisplayImage
    ADD COLUMN activeDisplayImageUniqueKey TINYINT
        GENERATED ALWAYS AS (
            IF((deletedAt IS NULL), 1, NULL)
        ) STORED,
    ADD CONSTRAINT UQ_DISPLAYIMAGE_ACTIVE_TYPE_SORT
        UNIQUE (displayId, imageType, sortOrder, activeDisplayImageUniqueKey);
