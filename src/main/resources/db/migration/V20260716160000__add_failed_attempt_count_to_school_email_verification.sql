ALTER TABLE SchoolEmailVerification
    ADD COLUMN failedAttemptCount INT NOT NULL DEFAULT 0
    AFTER verified;
