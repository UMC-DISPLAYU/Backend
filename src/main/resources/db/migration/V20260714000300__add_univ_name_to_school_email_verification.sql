ALTER TABLE SchoolEmailVerification
    ADD COLUMN univName VARCHAR(255) NOT NULL
    AFTER schoolEmail;