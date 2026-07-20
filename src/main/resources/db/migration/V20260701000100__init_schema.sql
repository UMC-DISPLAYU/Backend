create table DisplayU.Agreement
(
    agreeId    bigint auto_increment
        primary key,
    title      varchar(255)                             not null,
    type       enum ('SERVICE', 'PRIVACY', 'MARKETING') not null comment 'SERVICE, PRIVACY, MARKETING',
    content    text                                     not null,
    isRequired tinyint(1)                               not null,
    createdAt  datetime default CURRENT_TIMESTAMP       not null,
    updatedAt  datetime default CURRENT_TIMESTAMP       not null
);

create table DisplayU.ArtworkFeeling
(
    feelingId        bigint auto_increment
        primary key,
    content          text                               not null,
    createdAt        datetime default CURRENT_TIMESTAMP not null,
    updatedAt        datetime default CURRENT_TIMESTAMP not null,
    deletedAt        datetime                           null,
    displayArtworkId bigint                             not null,
    userId           bigint                             not null
);

create table DisplayU.ArtworkFeelingLike
(
    feelingLikeId bigint auto_increment
        primary key,
    createdAt     datetime default CURRENT_TIMESTAMP not null,
    updatedAt     datetime default CURRENT_TIMESTAMP not null,
    deletedAt     datetime                           null,
    feelingId     bigint                             not null,
    userId        bigint                             not null,
    constraint UK_ARTWORKFEELINGLIKE_FEELING_USER
        unique (feelingId, userId)
);

create table DisplayU.ArtworkFeelingReply
(
    feelingReplyId bigint auto_increment
        primary key,
    content        text                               not null,
    createdAt      datetime default CURRENT_TIMESTAMP not null,
    updatedAt      datetime default CURRENT_TIMESTAMP not null,
    deletedAt      datetime                           null,
    feelingId      bigint                             not null,
    userId         bigint                             null
);

create table DisplayU.ArtworkQuestion
(
    artQueId         bigint auto_increment
        primary key,
    content          varchar(255)                                           not null,
    isPublic         tinyint(1)                   default 1                 not null,
    answerStatus     enum ('WAITING', 'ANSWERED') default 'WAITING'         not null comment 'WAITING, ANSWERED',
    createdAt        datetime                     default CURRENT_TIMESTAMP not null,
    updatedAt        datetime                     default CURRENT_TIMESTAMP not null,
    deletedAt        datetime                                               null,
    displayArtworkId bigint                                                 not null,
    userId           bigint                                                 not null
);

create table DisplayU.ArtworkQuestionReply
(
    queReplyId bigint auto_increment
        primary key,
    content    text                               not null,
    createdAt  datetime default CURRENT_TIMESTAMP not null,
    updatedAt  datetime default CURRENT_TIMESTAMP not null,
    deletedAt  datetime                           null,
    artQueId   bigint                             not null,
    creatorId  bigint                             null,
    constraint UK_ARTWORKQUESTIONREPLY_ARTQUE
        unique (artQueId)
);

create table DisplayU.`Column`
(
    columnId       bigint auto_increment
        primary key,
    name           varchar(255)                       not null,
    content        text                               not null,
    columnImageUrl text                               not null,
    createdAt      datetime default CURRENT_TIMESTAMP not null,
    updatedAt      datetime default CURRENT_TIMESTAMP not null
);

create table DisplayU.Display
(
    displayId             bigint auto_increment
        primary key,
    title                 varchar(255)                                                                              not null,
    subtitle              varchar(255)                                                                              not null,
    content               text                                                                                      not null,
    placeName             varchar(255)                                                                              not null,
    latitude              decimal(10, 7)                                                                            not null,
    longitude             decimal(10, 7)                                                                            not null,
    qnaAccount            varchar(255)                                                                              not null,
    note                  text                                                                                      not null,
    organization          varchar(255)                                                                              not null,
    department            varchar(255)                                                                              not null,
    displayType           enum ('GRADUATION', 'ASSIGNMENTS', 'DEPARTMENTS', 'SMALL_GROUP', 'INTER_GROUP', 'OTHERS') not null comment 'GRADUATION, ASSIGNMENTS, DEPARTMENTS, SMALL_GROUP, INTER_GROUP, OTHERS',
    startDate             date                                                                                      not null,
    endDate               date                                                                                      not null,
    startTime             time                                                                                      not null,
    endTime               time                                                                                      not null,
    artWorkContentOpen    enum ('IMMEDIATELY', 'ON_EXHIBITION')                                                     not null comment 'IMMEDIATELY, ON_EXHIBITION',
    exhibitionContentOpen enum ('IMMEDIATELY', 'ON_EXHIBITION')                                                     not null comment 'IMMEDIATELY, ON_EXHIBITION',
    status                enum ('DRAFT', 'PUBLISHED')                         default 'DRAFT'                       not null comment 'DRAFT, PUBLISHED',
    invitationToken       varchar(255)                                                                              null,
    invitationDisabledAt  datetime                                                                                  null,
    createdAt             datetime                                                                                  not null,
    updatedAt             datetime                                                                                  not null,
    userId                bigint                                                                                    not null,
    region                enum ('ALL', 'SEOUL', 'GYEONGGI_INCHEON', 'OTHERS') default 'OTHERS'                      not null
);

create table DisplayU.DisplayContent
(
    displayContentId  bigint auto_increment
        primary key,
    imageUrl          varchar(255)                       null,
    width             int                                not null,
    height            int                                not null,
    contentsSortOrder int                                not null comment '0 : 대표 이미지',
    createdAt         datetime default CURRENT_TIMESTAMP not null,
    updatedAt         datetime default CURRENT_TIMESTAMP not null,
    categoryId        bigint                             not null
);

create table DisplayU.DisplayContentCategory
(
    categoryId        bigint auto_increment
        primary key,
    name              varchar(255)                       not null,
    description       varchar(255)                       not null,
    categorySortOrder int                                not null,
    createdAt         datetime default CURRENT_TIMESTAMP not null,
    updatedAt         datetime default CURRENT_TIMESTAMP not null,
    displayId         bigint                             not null
);

create table DisplayU.DisplayField
(
    displayFieldId bigint auto_increment
        primary key,
    field          enum ('PAINTING', 'DESIGN', 'PHOTOGRAPHY', 'ARCHITECTURE', 'VIDEO', 'CRAFTS', 'SCULPTURE', 'FASHION', 'INTERDISCIPLINARY', 'OTHERS') not null,
    sortOrder      int                                                                                                                                  not null,
    displayId      bigint                                                                                                                               not null,
    constraint UK_DISPLAYFIELD_DISPLAY_FIELD
        unique (displayId, field),
    constraint FK_DISPLAYFIELD_DISPLAY
        foreign key (displayId) references DisplayU.Display (displayId)
);

create table DisplayU.DisplayImage
(
    disImageId bigint auto_increment
        primary key,
    imageUrl   text                               not null,
    imageType  enum ('MAIN', 'DETAIL')            not null comment 'MAIN, DETAIL',
    width      int                                not null,
    height     int                                not null,
    sortOrder  int                                not null,
    createdAt  datetime default CURRENT_TIMESTAMP not null,
    updatedAt  datetime default CURRENT_TIMESTAMP not null,
    deletedAt  datetime                           null,
    displayId  bigint                             not null
);

create table DisplayU.DisplayInvitation
(
    disInvitationId bigint auto_increment
        primary key,
    userId          bigint   not null comment '전시의 팀 리더 아이디',
    displayId       bigint   not null,
    userId2         bigint   not null comment '초대 된 팀 멤버 아이디',
    createdAt       datetime not null,
    deletedAt       datetime null comment '초대 받은 팀원이 초대를 거절했을때 (비활성화)'
);

create table DisplayU.DisplayLike
(
    displayLikeId bigint auto_increment
        primary key,
    createdAt     datetime default CURRENT_TIMESTAMP not null,
    updatedAt     datetime default CURRENT_TIMESTAMP not null,
    deletedAt     datetime                           null,
    displayId     bigint                             not null,
    userId        bigint                             not null,
    constraint UQ_DISPLAYLIKE_DISPLAY_USER
        unique (displayId, userId),
    constraint FK_DISPLAYLIKE_DISPLAY
        foreign key (displayId) references DisplayU.Display (displayId)
);

create table DisplayU.DisplayReview
(
    displayReviewId bigint auto_increment
        primary key,
    content         text   not null,
    displayId       bigint not null,
    userId          bigint not null
);

create table DisplayU.DisplayReviewImage
(
    reviewImageId   bigint auto_increment
        primary key,
    imageUrl        text   not null,
    width           int    not null,
    height          int    not null,
    displayReviewId bigint not null
);

create table DisplayU.LoungeComment
(
    loungeCommentId bigint auto_increment
        primary key,
    content         text                                                  not null,
    commentStatus   enum ('ACTIVE', 'HIDDEN', 'DELETED') default 'ACTIVE' not null comment 'ACTIVE,HIDDEN,DELETED',
    createdAt       datetime                                              not null,
    updatedAt       datetime                                              not null,
    deletedAt       datetime                                              null,
    parentCommentId bigint                                                null,
    loungePostId    bigint                                                not null,
    userId          bigint                                                not null
);

create index IDX_LOUNGECOMMENT_REPLY_CURSOR
    on DisplayU.LoungeComment (parentCommentId, commentStatus, deletedAt, loungeCommentId);

create index IDX_LOUNGECOMMENT_ROOT_CURSOR
    on DisplayU.LoungeComment (loungePostId, parentCommentId, commentStatus, deletedAt, loungeCommentId);

create table DisplayU.LoungeCommentLike
(
    loungeCommentLikeId bigint auto_increment
        primary key,
    createdAt           datetime default CURRENT_TIMESTAMP not null,
    loungeCommentId     bigint                             not null,
    userId              bigint                             not null,
    constraint UK_LOUNGECOMMENTLIKE_COMMENT_USER
        unique (loungeCommentId, userId)
);

create index IDX_LOUNGECOMMENTLIKE_USER_COMMENT
    on DisplayU.LoungeCommentLike (userId, loungeCommentId);

create table DisplayU.LoungePost
(
    loungePostId bigint auto_increment
        primary key,
    title        varchar(255)                                                         not null,
    postImageUrl text                                                                 null,
    content      text                                                                 not null,
    postStatus   enum ('ACTIVE', 'HIDDEN', 'DELETED') default 'ACTIVE'                not null comment 'ACTIVE,HIDDEN,DELETED',
    category     enum ('DISPLAY_REVIEW', 'WORK_TIP', 'COLLABORATION', 'SPACE_RENTAL') not null comment 'DISPLAY_REVIEW, WORK_TIP, COLLABORATION, SPACE_RENTAL',
    createdAt    datetime                                                             not null,
    updatedAt    datetime                                                             not null,
    deletedAt    datetime                                                             null,
    userId       bigint                                                               not null
);

create index IDX_LOUNGEPOST_CATEGORY_CURSOR
    on DisplayU.LoungePost (postStatus, deletedAt, category, loungePostId);

create index IDX_LOUNGEPOST_CURSOR
    on DisplayU.LoungePost (postStatus, deletedAt, loungePostId);

create table DisplayU.LoungePostLike
(
    loungePostLikeId bigint auto_increment
        primary key,
    createdAt        datetime not null,
    loungePostId     bigint   not null,
    userId           bigint   not null,
    constraint UK_LOUNGEPOSTLIKE_POST_USER
        unique (loungePostId, userId)
);

create index IDX_LOUNGEPOSTLIKE_USER_POST
    on DisplayU.LoungePostLike (userId, loungePostId);

create table DisplayU.LoungePostScrap
(
    loungePostScrapId bigint auto_increment
        primary key,
    createdAt         datetime default CURRENT_TIMESTAMP not null,
    loungePostId      bigint                             not null,
    userId            bigint                             not null,
    constraint UK_LOUNGEPOSTSCRAP_POST_USER
        unique (loungePostId, userId)
);

create table DisplayU.MemoImage
(
    memoImageId bigint auto_increment
        primary key,
    imageUrl    text   not null,
    width       int    not null,
    height      int    not null,
    memoId      bigint not null
);

create table DisplayU.RefreshToken
(
    tokenId      bigint auto_increment
        primary key,
    refreshToken varchar(255)                       not null,
    createdAt    datetime default CURRENT_TIMESTAMP not null,
    updatedAt    datetime default CURRENT_TIMESTAMP not null,
    userId       bigint                             not null
);

create table DisplayU.TeamMember
(
    teamId          bigint auto_increment
        primary key,
    displayNickname varchar(255)                                        not null comment '실명 말고 닉네임을 보여주는 경우에 필요함.',
    author          enum ('TEAM_MEM', 'TEAM_LEADER') default 'TEAM_MEM' not null comment 'TEAM_MEM,  TEAM_LEADER',
    isAccepted      tinyint(1)                       default 0          not null,
    displayId       bigint                                              not null,
    userId          bigint                                              not null
);

create table DisplayU.User
(
    userId           bigint auto_increment
        primary key,
    provider         enum ('Google', 'Kakao')             not null comment 'Google, Kakao',
    providerId       varchar(255)                         not null,
    name             varchar(255)                         not null,
    nickname         varchar(255)                         not null,
    isVerified       tinyint(1) default 0                 not null,
    socialEmail      varchar(255)                         not null,
    schoolEmail      varchar(255)                         null,
    univ_name        varchar(255)                         null,
    createdAt        datetime   default CURRENT_TIMESTAMP not null,
    updatedAt        datetime   default CURRENT_TIMESTAMP not null,
    deletedAt        datetime                             null,
    nicknameChangeAt datetime   default CURRENT_TIMESTAMP null comment '닉네임 변경 날짜'
);

create table DisplayU.ArchiveDisplay
(
    archiveDisplayId bigint auto_increment
        primary key,
    displayId        bigint                             not null,
    userId           bigint                             not null,
    savedAt          datetime default CURRENT_TIMESTAMP not null,
    constraint UQ_ARCHIVEDISPLAY_USER_DISPLAY
        unique (userId, displayId),
    constraint FK_ARCHIVEDISPLAY_DISPLAY
        foreign key (displayId) references DisplayU.Display (displayId),
    constraint FK_ARCHIVEDISPLAY_USER
        foreign key (userId) references DisplayU.User (userId)
);

create table DisplayU.ArtistProfile
(
    artistProfileId bigint auto_increment
        primary key,
    userId          bigint                             not null,
    artistName      varchar(50)                        not null,
    schoolEmail     varchar(255)                       not null,
    univName        varchar(255)                       not null,
    portfolioUrl    varchar(255)                       null,
    createdAt       datetime default CURRENT_TIMESTAMP not null,
    updatedAt       datetime default CURRENT_TIMESTAMP not null,
    constraint UK_ARTISTPROFILE_ARTIST_NAME
        unique (artistName),
    constraint UK_ARTISTPROFILE_USER
        unique (userId),
    constraint FK_ARTISTPROFILE_USER
        foreign key (userId) references DisplayU.User (userId)
);

create table DisplayU.ArchiveArtist
(
    archiveArtistId bigint auto_increment
        primary key,
    userId          bigint                             not null,
    savedAt         datetime default CURRENT_TIMESTAMP not null,
    artistProfileId bigint                             not null,
    constraint UQ_ARCHIVEARTIST_USER_ARTISTPROFILE
        unique (userId, artistProfileId),
    constraint FK_ARCHIVEARTIST_ARTISTPROFILE
        foreign key (artistProfileId) references DisplayU.ArtistProfile (artistProfileId),
    constraint FK_ARCHIVEARTIST_USER
        foreign key (userId) references DisplayU.User (userId)
);

create table DisplayU.AreaOfActivity
(
    areaOfActivityId bigint auto_increment
        primary key,
    artistProfileId  bigint                             not null,
    field            varchar(255)                       not null,
    createdAt        datetime default CURRENT_TIMESTAMP not null,
    updatedAt        datetime default CURRENT_TIMESTAMP not null,
    constraint UK_AREA_OF_ACTIVITY_PROFILE_FIELD
        unique (artistProfileId, field),
    constraint FK_AREA_OF_ACTIVITY_ARTIST_PROFILE
        foreign key (artistProfileId) references DisplayU.ArtistProfile (artistProfileId)
);

create table DisplayU.DisplayArtwork
(
    displayArtworkId   bigint auto_increment
        primary key,
    artworkName        varchar(255)                                                                                                                    not null,
    content            text                                                                                                                            not null,
    type               enum ('PAINTING', 'DESIGN', 'PHOTOGRAPHY', 'ARCHITECTURE', 'VIDEO', 'CRAFTS', 'SCULPTURE', 'FASHION', 'ILLUSTRATION', 'OTHERS') not null comment 'PAINTING, DESIGN, PHOTOGRAPHY, ARCHITECTURE, VIDEO, CRAFTS, SCULPTURE, FASHION, ILLUSTRATION, OTHERS',
    productionYear     int                                                                                                                             not null,
    materialMedia      varchar(255)                                                                                                                    not null,
    size               varchar(255)                                                                                                                    not null,
    point              text                                                                                                                            not null,
    workSortOrder      int                                                                                                                             not null,
    createdAt          datetime default CURRENT_TIMESTAMP                                                                                              not null,
    updatedAt          datetime default CURRENT_TIMESTAMP                                                                                              not null,
    deletedAt          datetime                                                                                                                        null,
    displayId          bigint                                                                                                                          not null,
    registeredByUserId bigint                                                                                                                          not null,
    constraint FK_DISPLAYARTWORK_DISPLAY
        foreign key (displayId) references DisplayU.Display (displayId),
    constraint FK_DISPLAYARTWORK_REGISTEREDBY
        foreign key (registeredByUserId) references DisplayU.User (userId)
);

create table DisplayU.ArchiveWork
(
    archiveWorkId    bigint auto_increment
        primary key,
    displayArtworkId bigint                             not null,
    userId           bigint                             not null,
    savedAt          datetime default CURRENT_TIMESTAMP not null,
    constraint UQ_ARCHIVEWORK_USER_DISPLAYARTWORK
        unique (userId, displayArtworkId),
    constraint FK_ARCHIVEWORK_DISPLAYARTWORK
        foreign key (displayArtworkId) references DisplayU.DisplayArtwork (displayArtworkId),
    constraint FK_ARCHIVEWORK_USER
        foreign key (userId) references DisplayU.User (userId)
);

create table DisplayU.ArtworkImage
(
    artImageId       bigint auto_increment
        primary key,
    imageUrl         text                                                       not null,
    isThumbnail      tinyint(1)                       default 0                 not null,
    imageType        enum ('ARTWORK', 'WORK_PROCESS') default 'ARTWORK'         not null comment 'ARTWORK, WORK_PROCESS',
    sortOrder        int                                                        not null,
    caption          varchar(255)                                               null,
    width            int                                                        not null,
    height           int                                                        not null,
    createdAt        datetime                         default CURRENT_TIMESTAMP not null,
    updatedAt        datetime                         default CURRENT_TIMESTAMP not null,
    deletedAt        datetime                                                   null,
    displayArtworkId bigint                                                     not null,
    constraint FK_ARTWORKIMAGE_DISPLAYARTWORK
        foreign key (displayArtworkId) references DisplayU.DisplayArtwork (displayArtworkId)
);

create table DisplayU.Creator
(
    creatorId        bigint auto_increment
        primary key,
    creatorName      varchar(255)         not null,
    isContact        tinyint(1) default 0 not null,
    isLeader         tinyint(1) default 0 not null comment '권한과 상관 x',
    userId           bigint               null comment '직접 입력의 경우 null',
    displayArtworkId bigint               not null,
    constraint UK_CREATOR_DISPLAY_ARTWORK_USER
        unique (displayArtworkId, userId),
    constraint FK_CREATOR_DISPLAYARTWORK
        foreign key (displayArtworkId) references DisplayU.DisplayArtwork (displayArtworkId),
    constraint FK_CREATOR_USER
        foreign key (userId) references DisplayU.User (userId)
);

create table DisplayU.DisplayArtworkLike
(
    displayArtworkLikeId bigint auto_increment
        primary key,
    createdAt            datetime default CURRENT_TIMESTAMP not null,
    updatedAt            datetime default CURRENT_TIMESTAMP not null,
    deletedAt            datetime                           null,
    displayArtworkId     bigint                             not null,
    userId               bigint                             not null,
    constraint UQ_DISPLAYARTWORKLIKE_ARTWORK_USER
        unique (displayArtworkId, userId),
    constraint FK_DISPLAYARTWORKLIKE_DISPLAYARTWORK
        foreign key (displayArtworkId) references DisplayU.DisplayArtwork (displayArtworkId),
    constraint FK_DISPLAYARTWORKLIKE_USER
        foreign key (userId) references DisplayU.User (userId)
);

create table DisplayU.Memo
(
    memoId                 bigint auto_increment
        primary key,
    content                text                               not null,
    visitDate              date                               null,
    createdAt              datetime default CURRENT_TIMESTAMP not null,
    updatedAt              datetime default CURRENT_TIMESTAMP not null,
    deletedAt              datetime                           null,
    archiveDisplayId       bigint                             null,
    archiveWorkId          bigint                             null,
    activeArchiveDisplayId bigint as (if((`deletedAt` is null), `archiveDisplayId`, NULL)),
    activeArchiveWorkId    bigint as (if((`deletedAt` is null), `archiveWorkId`, NULL)),
    constraint UQ_MEMO_ACTIVE_ARCHIVEDISPLAY
        unique (activeArchiveDisplayId),
    constraint UQ_MEMO_ACTIVE_ARCHIVEWORK
        unique (activeArchiveWorkId),
    constraint FK_MEMO_ARCHIVEDISPLAY
        foreign key (archiveDisplayId) references DisplayU.ArchiveDisplay (archiveDisplayId),
    constraint FK_MEMO_ARCHIVEWORK
        foreign key (archiveWorkId) references DisplayU.ArchiveWork (archiveWorkId)
);

create table DisplayU.PersonalArtwork
(
    personalArtworkId bigint auto_increment
        primary key,
    artworkName       varchar(255)                                                                                                                    not null,
    content           text                                                                                                                            null,
    type              enum ('PAINTING', 'DESIGN', 'PHOTOGRAPHY', 'ARCHITECTURE', 'VIDEO', 'CRAFTS', 'SCULPTURE', 'FASHION', 'ILLUSTRATION', 'OTHERS') not null comment 'PAINTING, DESIGN, PHOTOGRAPHY, ARCHITECTURE, VIDEO, CRAFTS, SCULPTURE, FASHION, ILLUSTRATION, OTHERS',
    productionYear    int                                                                                                                             not null,
    materialMedia     varchar(255)                                                                                                                    not null,
    size              varchar(255)                                                                                                                    null,
    point             text                                                                                                                            null,
    createdAt         datetime default CURRENT_TIMESTAMP                                                                                              not null,
    updatedAt         datetime default CURRENT_TIMESTAMP                                                                                              not null,
    deletedAt         datetime                                                                                                                        null,
    userId            bigint                                                                                                                          not null,
    constraint FK_PERSONALARTWORK_USER
        foreign key (userId) references DisplayU.User (userId)
);

create index IDX_PERSONALARTWORK_USER_CREATEDAT
    on DisplayU.PersonalArtwork (userId, createdAt);

create table DisplayU.PersonalArtworkImage
(
    personalArtworkImageId bigint auto_increment
        primary key,
    imageUrl               text                                                       not null,
    isThumbnail            tinyint(1)                       default 0                 not null,
    imageType              enum ('ARTWORK', 'WORK_PROCESS') default 'ARTWORK'         not null comment 'ARTWORK, WORK_PROCESS',
    sortOrder              int                                                        not null,
    caption                varchar(255)                                               null,
    width                  int                                                        not null,
    height                 int                                                        not null,
    createdAt              datetime                         default CURRENT_TIMESTAMP not null,
    updatedAt              datetime                         default CURRENT_TIMESTAMP not null,
    personalArtworkId      bigint                                                     not null,
    constraint FK_PERSONALARTWORKIMAGE_PERSONALARTWORK
        foreign key (personalArtworkId) references DisplayU.PersonalArtwork (personalArtworkId)
);

create table DisplayU.PersonalArtworkLike
(
    personalArtworkLikeId bigint auto_increment
        primary key,
    createdAt             datetime default CURRENT_TIMESTAMP not null,
    updatedAt             datetime default CURRENT_TIMESTAMP not null,
    deletedAt             datetime                           null,
    personalArtworkId     bigint                             not null,
    userId                bigint                             not null,
    constraint UQ_PERSONALARTWORKLIKE_ARTWORK_USER
        unique (personalArtworkId, userId),
    constraint FK_PERSONALARTWORKLIKE_PERSONALARTWORK
        foreign key (personalArtworkId) references DisplayU.PersonalArtwork (personalArtworkId),
    constraint FK_PERSONALARTWORKLIKE_USER
        foreign key (userId) references DisplayU.User (userId)
);

create table DisplayU.SchoolEmailVerification
(
    verificationId     bigint auto_increment
        primary key,
    schoolEmail        varchar(255)                       not null,
    univName           varchar(255)                       not null,
    verificationCode   varchar(10)                        not null,
    expiresAt          datetime                           not null,
    sentAt             datetime                           not null,
    verified           tinyint(1)                         not null,
    failedAttemptCount int      default 0                 not null,
    createdAt          datetime default CURRENT_TIMESTAMP not null,
    updatedAt          datetime default CURRENT_TIMESTAMP not null,
    userId             bigint                             null,
    constraint FK_school_email_verification_user
        foreign key (userId) references DisplayU.User (userId)
);

create table DisplayU.UserAgreement
(
    userAgreeId bigint auto_increment
        primary key,
    isAgreed    tinyint(1)                         not null,
    agreedAt    datetime default CURRENT_TIMESTAMP not null,
    agreeId     bigint                             not null,
    userId      bigint                             not null
);
