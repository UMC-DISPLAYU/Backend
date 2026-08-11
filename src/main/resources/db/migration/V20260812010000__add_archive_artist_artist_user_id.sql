alter table DisplayU.ArchiveArtist
    add column artistUserId bigint null;

alter table DisplayU.ArchiveArtist
    add constraint FK_ARCHIVEARTIST_ARTISTUSER
        foreign key (artistUserId) references DisplayU.User (userId);
