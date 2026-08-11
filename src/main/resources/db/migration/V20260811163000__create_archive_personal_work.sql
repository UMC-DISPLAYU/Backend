create table DisplayU.ArchivePersonalWork
(
    archivePersonalWorkId bigint auto_increment
        primary key,
    personalArtworkId     bigint                             not null,
    userId                bigint                             not null,
    savedAt               datetime default CURRENT_TIMESTAMP not null,
    constraint UQ_ARCHIVEPERSONALWORK_USER_PERSONALARTWORK
        unique (userId, personalArtworkId),
    constraint FK_ARCHIVEPERSONALWORK_PERSONALARTWORK
        foreign key (personalArtworkId) references DisplayU.PersonalArtwork (personalArtworkId),
    constraint FK_ARCHIVEPERSONALWORK_USER
        foreign key (userId) references DisplayU.User (userId)
);
