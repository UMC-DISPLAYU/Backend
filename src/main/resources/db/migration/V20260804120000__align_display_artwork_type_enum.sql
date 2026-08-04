ALTER TABLE DisplayArtwork
    MODIFY COLUMN type enum (
        'PAINTING',
        'DESIGN',
        'PHOTOGRAPHY',
        'ARCHITECTURE',
        'VIDEO',
        'MEDIA',
        'CRAFTS',
        'CRAFT',
        'SCULPTURE',
        'FASHION',
        'ILLUSTRATION',
        'COMPLEX',
        'OTHERS',
        'ETC'
    ) NOT NULL COMMENT 'PAINTING, DESIGN, PHOTOGRAPHY, ARCHITECTURE, MEDIA, CRAFT, SCULPTURE, FASHION, COMPLEX, ETC';

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

UPDATE PersonalArtwork
SET type = 'MEDIA'
WHERE type = 'VIDEO';

UPDATE PersonalArtwork
SET type = 'CRAFT'
WHERE type = 'CRAFTS';

UPDATE PersonalArtwork
SET type = 'ETC'
WHERE type = 'ILLUSTRATION';

UPDATE PersonalArtwork
SET type = 'ETC'
WHERE type = 'OTHERS';

ALTER TABLE PersonalArtwork
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
