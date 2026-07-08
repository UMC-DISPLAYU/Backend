CREATE TABLE `DisplayContentCategory` (
                                          `categoryId`	BIGINT	NOT NULL,
                                          `name`	VARCHAR(255)	NOT NULL,
                                          `description`	VARCHAR(255)	NOT NULL,
                                          `categorySortOrder`	INT	NOT NULL,
                                          `createdAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                          `updatedAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                          `displayId`	BIGINT	NOT NULL
);

CREATE TABLE `Agreement` (
                             `agreeId`	BIGINT	NOT NULL,
                             `title`	VARCHAR(255)	NOT NULL,
                             `type`	ENUM('SERVICE', 'PRIVACY', 'MARKETING')	NOT NULL	COMMENT 'SERVICE, PRIVACY, MARKETING',
                             `content`	TEXT	NOT NULL,
                             `isRequired`	BOOLEAN	NOT NULL,
                             `createdAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                             `updatedAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `DisplayContent` (
                                  `displayContentId`	BIGINT	NOT NULL,
                                  `imageUrl`	VARCHAR(255)	NULL,
                                  `width`	INT	NOT NULL,
                                  `height`	INT	NOT NULL,
                                  `contentsSortOrder`	INT	NOT NULL	COMMENT '0 : 대표 이미지',
                                  `createdAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                  `updatedAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                  `categoryId`	BIGINT	NOT NULL
);

CREATE TABLE `TeamMember` (
                              `teamId`	BIGINT	NOT NULL,
                              `displayNickname`	VARCHAR(255)	NOT NULL	COMMENT '실명 말고 닉네임을 보여주는 경우에 필요함.',
                              `author`	ENUM('TEAM_MEM', 'TEAM_LEADER')	NOT NULL	DEFAULT 'TEAM_MEM'	COMMENT 'TEAM_MEM,  TEAM_LEADER',
                              `isAccepted`	BOOLEAN	NOT NULL	DEFAULT 0,
                              `displayId`	BIGINT	NOT NULL,
                              `userId`	BIGINT	NOT NULL
);

CREATE TABLE `LoungePost` (
                              `loungePostId`	BIGINT	NOT NULL,
                              `title`	VARCHAR(255)	NOT NULL,
                              `content`	TEXT	NOT NULL,
                              `postStatus`	ENUM('ACTIVE', 'HIDDEN', 'DELETED')	NOT NULL	DEFAULT 'ACTIVE'	COMMENT 'ACTIVE,HIDDEN,DELETED',
                              `category`	ENUM('DISPLAY_REVIEW', 'WORK_TIP', 'COLLABORATION')	NOT NULL	COMMENT 'DISPLAY_REVIEW, WORK_TIP, COLLABORATION',
                              `createdAt`	DATETIME	NOT NULL,
                              `updatedAt`	DATETIME	NOT NULL,
                              `deletedAt`	DATETIME	NULL,
                              `userId`	BIGINT	NOT NULL
);

CREATE TABLE `ArtworkFeelingReply` (
                                       `feelingReplyId`	BIGINT	NOT NULL,
                                       `content`	TEXT	NOT NULL,
                                       `createdAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                       `updatedAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                       `deletedAt`	DATETIME	NULL,
                                       `feelingId`	BIGINT	NOT NULL
);

CREATE TABLE `ArchiveArtist` (
                                 `archiveArtistId`	BIGINT	NOT NULL,
                                 `userId`	BIGINT	NOT NULL
);

CREATE TABLE `ArchiveDisplay` (
                                  `archiveDisplayId`	BIGINT	NOT NULL,
                                  `displayId`	BIGINT	NOT NULL
);

CREATE TABLE `ArtworkQuestionReply` (
                                        `queReplyId`	BIGINT	NOT NULL,
                                        `content`	TEXT	NOT NULL,
                                        `createdAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                        `updatedAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                        `deletedAt`	DATETIME	NULL,
                                        `artQueId`	BIGINT	NOT NULL
);

CREATE TABLE `DisplayReviewImage` (
                                      `reviewImageId`	BIGINT	NOT NULL,
                                      `imageUrl`	TEXT	NOT NULL,
                                      `width`	INT	NOT NULL,
                                      `height`	INT	NOT NULL,
                                      `displayReviewId`	BIGINT	NOT NULL
);

CREATE TABLE `RefreshToken` (
                                `tokenId`	BIGINT	NOT NULL,
                                `refreshToken`	VARCHAR(255)	NOT NULL,
                                `createdAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                `updatedAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                `userId`	BIGINT	NOT NULL
);

CREATE TABLE `MemoImage` (
                             `memoImageId`	BIGINT	NOT NULL,
                             `imageUrl`	TEXT	NOT NULL,
                             `width`	INT	NOT NULL,
                             `height`	INT	NOT NULL,
                             `memoId`	BIGINT	NOT NULL
);

CREATE TABLE `DisplayInvitation` (
                                     `disInvitationId`	BIGINT	NOT NULL,
                                     `userId`	BIGINT	NOT NULL	COMMENT '전시의 팀 리더 아이디',
                                     `displayId`	BIGINT	NOT NULL,
                                     `userId2`	BIGINT	NOT NULL	COMMENT '초대 된 팀 멤버 아이디',
                                     `createdAt`	DATETIME	NOT NULL,
                                     `deletedAt`	DATETIME	NULL	COMMENT '초대 받은 팀원이 초대를 거절했을때 (비활성화)'
);

CREATE TABLE `ArchiveWork` (
                               `archiveWorkId`	BIGINT	NOT NULL,
                               `displayArtworkId`	BIGINT	NOT NULL
);

CREATE TABLE `ArtworkFeelingLike` (
                                      `areLikeId`	VARCHAR(255)	NOT NULL,
                                      `createdAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                      `feelingId`	VARCHAR(255)	NOT NULL,
                                      `userId`	BIGINT	NOT NULL
);

CREATE TABLE `DisplayLike` (
                               `dislikeId`	VARCHAR(255)	NOT NULL,
                               `createdAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                               `displayId`	BIGINT	NOT NULL,
                               `userId`	BIGINT	NOT NULL
);

CREATE TABLE `DisplayArtwork` (
                                  `displayArtworkId`	BIGINT	NOT NULL,
                                  `artworkName`	VARCHAR(255)	NOT NULL,
                                  `content`	TEXT	NOT NULL,
                                  `type`	ENUM('PAINTING', 'DESIGN', 'PHOTOGRAPHY', 'ARCHITECTURE', 'VIDEO', 'CRAFTS', 'SCULPTURE', 'FASHION', 'ILLUSTRATION', 'OTHERS')	NOT NULL	COMMENT 'PAINTING, DESIGN, PHOTOGRAPHY, ARCHITECTURE, VIDEO, CRAFTS, SCULPTURE, FASHION, ILLUSTRATION, OTHERS',
                                  `productionYear`	INT	NOT NULL,
                                  `materialMedia`	VARCHAR(255)	NOT NULL,
                                  `size`	VARCHAR(255)	NOT NULL,
                                  `point`	TEXT	NOT NULL,
                                  `workSortOrder`	INT	NOT NULL,
                                  `createdAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                  `updatedAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                  `deletedAt`	DATETIME	NULL,
                                  `displayId`	BIGINT	NOT NULL
);

CREATE TABLE `ArtworkImage` (
                                `artImageId`	BIGINT	NOT NULL,
                                `imageUrl`	TEXT	NOT NULL,
                                `isThumbnail`	BOOLEAN	NOT NULL	DEFAULT FALSE,
                                `sortOrder`	INT	NOT NULL,
                                `caption`	VARCHAR(255)	NULL,
                                `width`	INT	NOT NULL,
                                `height`	INT	NOT NULL,
                                `createdAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                `updatedAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                `deletedAt`	DATETIME	NULL,
                                `displayArtworkId`	BIGINT	NOT NULL
);

CREATE TABLE `LoungePostLike` (
                                  `loungePostLikeId`	BIGINT	NOT NULL,
                                  `createdAt`	DATETIME	NOT NULL,
                                  `loungePostId`	BIGINT	NOT NULL,
                                  `userId`	BIGINT	NOT NULL
);

CREATE TABLE `Creator` (
                           `creatorId`	BIGINT	NOT NULL,
                           `creatorName`	VARCHAR(255)	NOT NULL,
                           `isContact`	BOOLEAN	NOT NULL	DEFAULT FALSE,
                           `isLeader`	BOOLEAN	NOT NULL	DEFAULT FALSE	COMMENT '권한과 상관 x',
                           `userId`	BIGINT	NULL	COMMENT '직접 입력의 경우 null',
                           `displayArtworkId`	BIGINT	NOT NULL
);

CREATE TABLE `UserAgreement` (
                                 `userAgreeId`	BIGINT	NOT NULL,
                                 `isAgreed`	BOOLEAN	NOT NULL,
                                 `agreedAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                 `agreeId`	BIGINT	NOT NULL,
                                 `userId`	BIGINT	NOT NULL
);

CREATE TABLE `ArtworkQuestion` (
                                   `artQueId`	BIGINT	NOT NULL,
                                   `content`	VARCHAR(255)	NOT NULL,
                                   `isPublic`	BOOLEAN	NOT NULL	DEFAULT TRUE,
                                   `answerStatus`	ENUM('WAITING', 'ANSWERED')	NOT NULL	DEFAULT 'WAITING'	COMMENT 'WAITING, ANSWERED',
                                   `createdAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                   `updatedAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                   `deletedAt`	DATETIME	NULL,
                                   `displayArtworkId`	BIGINT	NOT NULL,
                                   `userId`	BIGINT	NOT NULL
);

CREATE TABLE `Display` (
                           `displayId`	BIGINT	NOT NULL,
                           `title`	VARCHAR(255)	NOT NULL,
                           `subtitle`	VARCHAR(255)	NOT NULL,
                           `content`	TEXT	NOT NULL,
                           `placeName`	VARCHAR(255)	NOT NULL,
                           `latitude`	DECIMAL(10, 7)	NOT NULL,
                           `longitude`	DECIMAL(10, 7)	NOT NULL,
                           `qnaAccount`	VARCHAR(255)	NOT NULL,
                           `note`	TEXT	NOT NULL,
                           `organization`	VARCHAR(255)	NOT NULL,
                           `department`	VARCHAR(255)	NOT NULL,
                           `displayType`	ENUM('GRADUATION', 'ASSIGNMENTS', 'DEPARTMENTS', 'SMALL_GROUP', 'INTER_GROUP', 'OTHERS')	NOT NULL	COMMENT 'GRADUATION, ASSIGNMENTS, DEPARTMENTS, SMALL_GROUP, INTER_GROUP, OTHERS',
                           `displayField`	ENUM('PAINTING', 'DESIGN', 'PHOTOGRAPHY', 'ARCHITECTURE', 'VIDEO', 'CRAFTS', 'SCULPTURE', 'FASHION', 'INTERDISCIPLINARY', 'OTHERS')	NOT NULL	COMMENT 'PAINTING, DESIGN, PHOTOGRAPHY, ARCHITECTURE, VIDEO, CRAFTS, SCULPTURE, FASHION, INTERDISCIPLINARY, OTHERS',
                           `startDate`	DATE	NOT NULL,
                           `endDate`	DATE	NOT NULL,
                           `startTime`	TIME	NOT NULL,
                           `endTime`	TIME	NOT NULL,
                           `artWorkContentOpen`	ENUM('IMMEDIATELY', 'ON_EXHIBITION')	NOT NULL	COMMENT 'IMMEDIATELY, ON_EXHIBITION',
                           `exhibitionContentOpen`	ENUM('IMMEDIATELY', 'ON_EXHIBITION')	NOT NULL	COMMENT 'IMMEDIATELY, ON_EXHIBITION',
                           `status`	ENUM('DRAFT', 'PUBLISHED')	NOT NULL	DEFAULT 'DRAFT'	COMMENT 'DRAFT, PUBLISHED',
                           `invitationToken`	VARCHAR(255)	NULL,
                           `invitationDisabledAt`	DATETIME	NULL	DEFAULT NULL,
                           `createdAt`	DATETIME	NOT NULL,
                           `updatedAt`	DATETIME	NOT NULL,
                           `userId`	BIGINT	NOT NULL
);

CREATE TABLE `Column` (
                          `columnId`	BIGINT	NOT NULL,
                          `name`	VARCHAR(255)	NOT NULL,
                          `content`	TEXT	NOT NULL,
                          `columnImageUrl`	TEXT	NOT NULL,
                          `createdAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                          `updatedAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `ArtworkFeeling` (
                                  `feelingId`	VARCHAR(255)	NOT NULL,
                                  `content`	TEXT	NOT NULL,
                                  `createdAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                  `updatedAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                  `deletedAt`	DATETIME	NULL,
                                  `displayArtworkId`	BIGINT	NOT NULL,
                                  `userId`	BIGINT	NOT NULL
);

CREATE TABLE `Memo` (
                        `memoId`	BIGINT	NOT NULL,
                        `content`	TEXT	NOT NULL,
                        `visitDate`	DATE	NULL,
                        `createdAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                        `updatedAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                        `deletedAt`	DATETIME	NULL,
                        `archiveAgreeId`	BIGINT	NULL,
                        `archiveWorkId`	BIGINT	NULL
);

CREATE TABLE `User` (
                        `userId`	BIGINT	NOT NULL,
                        `provider`	ENUM('Google', 'Kakao')	NOT NULL	COMMENT 'Google, Kakao',
                        `providerId`	VARCHAR(255)	NOT NULL,
                        `name`	VARCHAR(255)	NOT NULL,
                        `nickname`	VARCHAR(255)	NOT NULL,
                        `isVerified`	BOOLEAN	NOT NULL	DEFAULT FALSE,
                        `socialEmail`	VARCHAR(255)	NOT NULL,
                        `schoolEmail`	VARCHAR(255)	NULL,
                        `createdAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                        `updatedAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                        `deletedAt`	DATETIME	NULL,
                        `nicknameChangeAt`	DATETIME	NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '닉네임 변경 날짜'
);

CREATE TABLE `DisplayReview` (
                                 `displayReviewId`	BIGINT	NOT NULL,
                                 `content`	TEXT	NOT NULL,
                                 `displayId`	BIGINT	NOT NULL,
                                 `userId`	BIGINT	NOT NULL
);

CREATE TABLE `DisplayImage` (
                                `disImageId`	BIGINT	NOT NULL,
                                `imageUrl`	TEXT	NOT NULL,
                                `imageType`	ENUM('MAIN', 'DETAIL')	NOT NULL	COMMENT 'MAIN, DETAIL',
                                `width`	INT	NOT NULL,
                                `height`	INT	NOT NULL,
                                `sortOrder`	INT	NOT NULL,
                                `createdAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                `updatedAt`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                `deletedAt`	DATETIME	NULL	DEFAULT NULL,
                                `displayId`	BIGINT	NOT NULL
);

CREATE TABLE `LoungeComment` (
                                 `loungeCommentId`	BIGINT	NOT NULL,
                                 `content`	TEXT	NOT NULL,
                                 `commentStatus`	ENUM('ACTIVE', 'HIDDEN', 'DELETED')	NOT NULL	DEFAULT 'ACTIVE'	COMMENT 'ACTIVE,HIDDEN,DELETED',
                                 `createdAt`	DATETIME	NOT NULL,
                                 `updatedAt`	DATETIME	NOT NULL,
                                 `deletedAt`	DATETIME	NULL,
                                 `loungePostId`	BIGINT	NOT NULL,
                                 `userId`	BIGINT	NOT NULL
);

ALTER TABLE `DisplayContentCategory` ADD CONSTRAINT `PK_DISPLAYCONTENTCATEGORY` PRIMARY KEY (
                                                                                             `categoryId`
    );

ALTER TABLE `Agreement` ADD CONSTRAINT `PK_AGREEMENT` PRIMARY KEY (
                                                                   `agreeId`
    );

ALTER TABLE `DisplayContent` ADD CONSTRAINT `PK_DISPLAYCONTENT` PRIMARY KEY (
                                                                             `displayContentId`
    );

ALTER TABLE `TeamMember` ADD CONSTRAINT `PK_TEAMMEMBER` PRIMARY KEY (
                                                                     `teamId`
    );

ALTER TABLE `LoungePost` ADD CONSTRAINT `PK_LOUNGEPOST` PRIMARY KEY (
                                                                     `loungePostId`
    );

ALTER TABLE `ArtworkFeelingReply` ADD CONSTRAINT `PK_ARTWORKFEELINGREPLY` PRIMARY KEY (
                                                                                       `feelingReplyId`
    );

ALTER TABLE `ArchiveArtist` ADD CONSTRAINT `PK_ARCHIVEARTIST` PRIMARY KEY (
                                                                           `archiveArtistId`
    );

ALTER TABLE `ArchiveDisplay` ADD CONSTRAINT `PK_ARCHIVEDISPLAY` PRIMARY KEY (
                                                                             `archiveDisplayId`
    );

ALTER TABLE `ArtworkQuestionReply` ADD CONSTRAINT `PK_ARTWORKQUESTIONREPLY` PRIMARY KEY (
                                                                                         `queReplyId`
    );

ALTER TABLE `DisplayReviewImage` ADD CONSTRAINT `PK_DISPLAYREVIEWIMAGE` PRIMARY KEY (
                                                                                     `reviewImageId`
    );

ALTER TABLE `RefreshToken` ADD CONSTRAINT `PK_REFRESHTOKEN` PRIMARY KEY (
                                                                         `tokenId`
    );

ALTER TABLE `MemoImage` ADD CONSTRAINT `PK_MEMOIMAGE` PRIMARY KEY (
                                                                   `memoImageId`
    );

ALTER TABLE `DisplayInvitation` ADD CONSTRAINT `PK_DISPLAYINVITATION` PRIMARY KEY (
                                                                                   `disInvitationId`
    );

ALTER TABLE `ArchiveWork` ADD CONSTRAINT `PK_ARCHIVEWORK` PRIMARY KEY (
                                                                       `archiveWorkId`
    );

ALTER TABLE `ArtworkFeelingLike` ADD CONSTRAINT `PK_ARTWORKFEELINGLIKE` PRIMARY KEY (
                                                                                     `areLikeId`
    );

ALTER TABLE `DisplayLike` ADD CONSTRAINT `PK_DISPLAYLIKE` PRIMARY KEY (
                                                                       `dislikeId`
    );

ALTER TABLE `DisplayArtwork` ADD CONSTRAINT `PK_DISPLAYARTWORK` PRIMARY KEY (
                                                                             `displayArtworkId`
    );

ALTER TABLE `ArtworkImage` ADD CONSTRAINT `PK_ARTWORKIMAGE` PRIMARY KEY (
                                                                         `artImageId`
    );

ALTER TABLE `LoungePostLike` ADD CONSTRAINT `PK_LOUNGEPOSTLIKE` PRIMARY KEY (
                                                                             `loungePostLikeId`
    );

ALTER TABLE `Creator` ADD CONSTRAINT `PK_CREATOR` PRIMARY KEY (
                                                               `creatorId`
    );

ALTER TABLE `UserAgreement` ADD CONSTRAINT `PK_USERAGREEMENT` PRIMARY KEY (
                                                                           `userAgreeId`
    );

ALTER TABLE `ArtworkQuestion` ADD CONSTRAINT `PK_ARTWORKQUESTION` PRIMARY KEY (
                                                                               `artQueId`
    );

ALTER TABLE `Display` ADD CONSTRAINT `PK_DISPLAY` PRIMARY KEY (
                                                               `displayId`
    );

ALTER TABLE `Column` ADD CONSTRAINT `PK_COLUMN` PRIMARY KEY (
                                                             `columnId`
    );

ALTER TABLE `ArtworkFeeling` ADD CONSTRAINT `PK_ARTWORKFEELING` PRIMARY KEY (
                                                                             `feelingId`
    );

ALTER TABLE `Memo` ADD CONSTRAINT `PK_MEMO` PRIMARY KEY (
                                                         `memoId`
    );

ALTER TABLE `User` ADD CONSTRAINT `PK_USER` PRIMARY KEY (
                                                         `userId`
    );

ALTER TABLE `DisplayReview` ADD CONSTRAINT `PK_DISPLAYREVIEW` PRIMARY KEY (
                                                                           `displayReviewId`
    );

ALTER TABLE `DisplayImage` ADD CONSTRAINT `PK_DISPLAYIMAGE` PRIMARY KEY (
                                                                         `disImageId`
    );

ALTER TABLE `LoungeComment` ADD CONSTRAINT `PK_LOUNGECOMMENT` PRIMARY KEY (
                                                                           `loungeCommentId`
    );
