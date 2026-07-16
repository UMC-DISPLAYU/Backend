ALTER TABLE SchoolEmailVerification
    ADD COLUMN sentAt DATETIME NULL AFTER expiresAt,
    ADD COLUMN verified BOOLEAN NULL AFTER sentAt;


UPDATE SchoolEmailVerification
SET
    sentAt = createdAt,
    verified = false
WHERE sentAt IS NULL
   OR verified IS NULL;


ALTER TABLE SchoolEmailVerification
    MODIFY COLUMN sentAt DATETIME NOT NULL,
    MODIFY COLUMN verified BOOLEAN NOT NULL;