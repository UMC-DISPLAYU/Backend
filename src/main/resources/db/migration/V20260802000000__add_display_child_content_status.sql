ALTER TABLE DisplayContent
    ADD COLUMN status enum ('DRAFT', 'PUBLISHED') NOT NULL DEFAULT 'PUBLISHED';

ALTER TABLE DisplayArtwork
    ADD COLUMN status enum ('DRAFT', 'PUBLISHED') NOT NULL DEFAULT 'PUBLISHED';

CREATE INDEX IDX_DISPLAYCONTENT_PUBLICATION
    ON DisplayContent (status, categoryId, contentsSortOrder, displayContentId);

CREATE INDEX IDX_DISPLAYARTWORK_PUBLICATION
    ON DisplayArtwork (status, displayId, deletedAt, workSortOrder, displayArtworkId);
