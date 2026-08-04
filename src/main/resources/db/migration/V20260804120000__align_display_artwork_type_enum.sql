UPDATE DisplayArtwork
SET type = 'MEDIA'
WHERE type = 'VIDEO';

UPDATE DisplayArtwork
SET type = 'CRAFT'
WHERE type = 'CRAFTS';

UPDATE DisplayArtwork
SET type = 'ETC'
WHERE type = 'ILLUSTRATION';

UPDATE DisplayArtwork
SET type = 'ETC'
WHERE type = 'OTHERS';

ALTER TABLE DisplayArtwork
    MODIFY COLUMN type enum (
        'PAINTING',
        'DESIGN',
        'PHOTOGRAPHY',
        'ARCHITECTURE',
        'MEDIA',
        'CRAFT',
        'SCULPTURE',
        'FASHION',
        'COMPLEX',
        'ETC'
    ) NOT NULL COMMENT 'PAINTING, DESIGN, PHOTOGRAPHY, ARCHITECTURE, MEDIA, CRAFT, SCULPTURE, FASHION, COMPLEX, ETC';
