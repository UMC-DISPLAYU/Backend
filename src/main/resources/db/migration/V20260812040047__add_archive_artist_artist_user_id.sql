alter table ArchiveArtist
    add column artistUserId bigint null;

-- artistProfileId는 FK_ARCHIVEARTIST_ARTISTPROFILE로 항상 유효한 ArtistProfile을 가리키므로,
-- 기존 행 전부 백필 가능하다. 백필 없이 두면 DeleteArchiveArtistService가
-- findByUserIdAndArtistUserId로만 조회하게 되어 기존 저장 기록을 아무도 취소할 수 없게 된다.
update ArchiveArtist aa
    join ArtistProfile ap on ap.artistProfileId = aa.artistProfileId
set aa.artistUserId = ap.userId
where aa.artistUserId is null;

-- 백필로 모든 행이 채워졌으므로, 이후 앱 코드 실수로 NULL이 다시 저장되는 것을 DB 차원에서 막는다.
alter table ArchiveArtist
    modify column artistUserId bigint not null;

alter table ArchiveArtist
    add constraint FK_ARCHIVEARTIST_ARTISTUSER
        foreign key (artistUserId) references User (userId);
