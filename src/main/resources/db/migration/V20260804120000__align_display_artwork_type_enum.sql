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

-- PersonalArtwork도 DisplayArtwork과 동일하게 "넓히기 → 변환 → 좁히기" 순서로 처리한다.
-- 넓히기 없이 UPDATE부터 하면 enum에 없는 값(MEDIA 등)을 넣게 되어 Data truncated로 실패한다.
ALTER TABLE PersonalArtwork
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
    ) NOT NULL;

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
