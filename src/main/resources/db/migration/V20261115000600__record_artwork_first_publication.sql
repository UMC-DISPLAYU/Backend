ALTER TABLE DisplayArtwork ADD COLUMN firstPublishedAt DATETIME(6) NULL;
UPDATE DisplayArtwork SET firstPublishedAt = createdAt WHERE status = 'PUBLISHED';
CREATE INDEX idx_artwork_display_publication ON DisplayArtwork(displayId, status, deletedAt, firstPublishedAt, displayArtworkId);
