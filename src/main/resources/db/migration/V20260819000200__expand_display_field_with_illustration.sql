ALTER TABLE DisplayField
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
