ALTER TABLE display_field
    MODIFY COLUMN field ENUM (
        'PAINTING',
        'DESIGN',
        'PHOTOGRAPHY',
        'ARCHITECTURE',
        'VIDEO',
        'CRAFTS',
        'SCULPTURE',
        'FASHION',
        'INTERDISCIPLINARY',
        'ILLUSTRATION',
        'OTHERS'
    ) NOT NULL;

UPDATE display_field
SET field = 'ILLUSTRATION'
WHERE field = 'INTERDISCIPLINARY';

ALTER TABLE display_field
    MODIFY COLUMN field ENUM (
        'PAINTING',
        'DESIGN',
        'PHOTOGRAPHY',
        'ARCHITECTURE',
        'VIDEO',
        'CRAFTS',
        'SCULPTURE',
        'FASHION',
        'ILLUSTRATION',
        'OTHERS'
    ) NOT NULL;
