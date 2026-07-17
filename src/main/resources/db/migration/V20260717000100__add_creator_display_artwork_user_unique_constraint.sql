ALTER TABLE `Creator`
    ADD CONSTRAINT `UK_CREATOR_DISPLAY_ARTWORK_USER`
        UNIQUE (`displayArtworkId`, `userId`);
