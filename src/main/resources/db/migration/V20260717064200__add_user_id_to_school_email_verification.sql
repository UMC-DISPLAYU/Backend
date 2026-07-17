ALTER TABLE SchoolEmailVerification
    ADD COLUMN userId BIGINT NULL;


ALTER TABLE SchoolEmailVerification
    ADD CONSTRAINT FK_school_email_verification_user
        FOREIGN KEY (userId)
            REFERENCES User(userId);