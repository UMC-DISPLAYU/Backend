-- Lounge 조회 API 부하 테스트용 데이터 생성 스크립트
--
-- 주의:
-- 1. 초기화된 로컬 DB에서만 실행한다.
-- 2. 최소 3명의 사용자가 필요하다.
-- 3. 중복 실행을 지원하지 않으므로 한 번만 실행한다.
-- 4. 운영 또는 공유 DB에서 실행하지 않는다.

SET SESSION cte_max_recursion_depth = 50000;

SET @perf_user_1 = (
    SELECT userId
    FROM `User`
    ORDER BY userId
    LIMIT 1
);

SET @perf_user_2 = (
    SELECT userId
    FROM `User`
    ORDER BY userId
    LIMIT 1 OFFSET 1
);

SET @perf_user_3 = (
    SELECT userId
    FROM `User`
    ORDER BY userId
    LIMIT 1 OFFSET 2
);

-- 게시글 50,000건
INSERT INTO LoungePost (
    title,
    content,
    postStatus,
    category,
    createdAt,
    updatedAt,
    deletedAt,
    userId
)
WITH RECURSIVE numbers AS (
    SELECT 1 AS number

    UNION ALL

    SELECT number + 1
    FROM numbers
    WHERE number < 50000
)
SELECT
    CONCAT('DU102-PERF-', number),
    CONCAT(
            '라운지 성능 테스트용 게시글 ',
            number,
            ' ',
            REPEAT('테스트 내용 ', 50)
    ),
    'ACTIVE',
    ELT(
            MOD(number - 1, 4) + 1,
            'DISPLAY_REVIEW',
            'WORK_TIP',
            'COLLABORATION',
            'SPACE_RENTAL'
    ),
    NOW(),
    NOW(),
    NULL,
    CASE MOD(number - 1, 3)
        WHEN 0 THEN @perf_user_1
        WHEN 1 THEN @perf_user_2
        ELSE @perf_user_3
        END
FROM numbers;

COMMIT;

-- 게시글당 이미지 0~3개
INSERT INTO LoungePostImage (
    loungePostId,
    imageUrl,
    sortOrder,
    createdAt,
    updatedAt
)
SELECT
    post.loungePostId,
    CONCAT(
            'https://example.com/perf/lounge/',
            post.loungePostId,
            '/',
            imageOrder.sortOrder,
            '.jpg'
    ),
    imageOrder.sortOrder,
    NOW(),
    NOW()
FROM LoungePost post
         JOIN (
    SELECT 0 AS sortOrder
    UNION ALL SELECT 1
    UNION ALL SELECT 2
) imageOrder
              ON imageOrder.sortOrder < MOD(post.loungePostId, 4)
WHERE post.title LIKE 'DU102-PERF-%';

COMMIT;

-- 게시글당 루트 댓글 0~5개
INSERT INTO LoungeComment (
    content,
    commentStatus,
    createdAt,
    updatedAt,
    deletedAt,
    parentCommentId,
    loungePostId,
    userId
)
SELECT
    CONCAT(
            'DU102-PERF-COMMENT-',
            post.loungePostId,
            '-',
            commentOrder.number
    ),
    'ACTIVE',
    NOW(),
    NOW(),
    NULL,
    NULL,
    post.loungePostId,
    CASE MOD(post.loungePostId + commentOrder.number, 3)
        WHEN 0 THEN @perf_user_1
        WHEN 1 THEN @perf_user_2
        ELSE @perf_user_3
        END
FROM LoungePost post
         JOIN (
    SELECT 0 AS number
    UNION ALL SELECT 1
    UNION ALL SELECT 2
    UNION ALL SELECT 3
    UNION ALL SELECT 4
) commentOrder
              ON commentOrder.number < MOD(post.loungePostId, 6)
WHERE post.title LIKE 'DU102-PERF-%';

COMMIT;

-- 루트 댓글당 답글 0~2개
INSERT INTO LoungeComment (
    content,
    commentStatus,
    createdAt,
    updatedAt,
    deletedAt,
    parentCommentId,
    loungePostId,
    userId
)
SELECT
    CONCAT(
            'DU102-PERF-REPLY-',
            parent.loungeCommentId,
            '-',
            replyOrder.number
    ),
    'ACTIVE',
    NOW(),
    NOW(),
    NULL,
    parent.loungeCommentId,
    parent.loungePostId,
    CASE MOD(parent.loungeCommentId + replyOrder.number, 3)
        WHEN 0 THEN @perf_user_1
        WHEN 1 THEN @perf_user_2
        ELSE @perf_user_3
        END
FROM LoungeComment parent
         JOIN (
    SELECT 0 AS number
    UNION ALL SELECT 1
) replyOrder
              ON replyOrder.number < MOD(parent.loungeCommentId, 3)
WHERE parent.content LIKE 'DU102-PERF-COMMENT-%'
  AND parent.parentCommentId IS NULL;

COMMIT;

-- 게시글당 좋아요 0~3개
INSERT INTO LoungePostLike (
    createdAt,
    loungePostId,
    userId
)
SELECT
    NOW(),
    post.loungePostId,
    testUser.userId
FROM LoungePost post
         JOIN (
    SELECT 0 AS number, @perf_user_1 AS userId
    UNION ALL
    SELECT 1, @perf_user_2
    UNION ALL
    SELECT 2, @perf_user_3
) testUser
              ON testUser.number < MOD(post.loungePostId, 4)
WHERE post.title LIKE 'DU102-PERF-%';

COMMIT;

-- 테스트 게시글의 절반에 스크랩 생성
INSERT INTO LoungePostScrap (
    createdAt,
    loungePostId,
    userId
)
SELECT
    NOW(),
    post.loungePostId,
    CASE MOD(post.loungePostId, 3)
        WHEN 0 THEN @perf_user_1
        WHEN 1 THEN @perf_user_2
        ELSE @perf_user_3
        END
FROM LoungePost post
WHERE post.title LIKE 'DU102-PERF-%'
  AND MOD(post.loungePostId, 2) = 0;

COMMIT;

-- 댓글과 답글당 좋아요 0~3개
INSERT INTO LoungeCommentLike (
    createdAt,
    loungeCommentId,
    userId
)
SELECT
    NOW(),
    comment.loungeCommentId,
    testUser.userId
FROM LoungeComment comment
         JOIN (
    SELECT 0 AS number, @perf_user_1 AS userId
    UNION ALL
    SELECT 1, @perf_user_2
    UNION ALL
    SELECT 2, @perf_user_3
) testUser
              ON testUser.number < MOD(comment.loungeCommentId, 4)
WHERE comment.content LIKE 'DU102-PERF-%';

COMMIT;

-- 생성 결과 확인
SELECT
    'LoungePost' AS tableName,
    COUNT(*) AS rowCount
FROM LoungePost
WHERE title LIKE 'DU102-PERF-%'

UNION ALL

SELECT
    'LoungePostImage',
    COUNT(*)
FROM LoungePostImage image
         JOIN LoungePost post
              ON post.loungePostId = image.loungePostId
WHERE post.title LIKE 'DU102-PERF-%'

UNION ALL

SELECT
    'LoungeRootComment',
    COUNT(*)
FROM LoungeComment
WHERE content LIKE 'DU102-PERF-COMMENT-%'
  AND parentCommentId IS NULL

UNION ALL

SELECT
    'LoungeReply',
    COUNT(*)
FROM LoungeComment
WHERE content LIKE 'DU102-PERF-REPLY-%'
  AND parentCommentId IS NOT NULL

UNION ALL

SELECT
    'LoungePostLike',
    COUNT(*)
FROM LoungePostLike postLike
         JOIN LoungePost post
              ON post.loungePostId = postLike.loungePostId
WHERE post.title LIKE 'DU102-PERF-%'

UNION ALL

SELECT
    'LoungePostScrap',
    COUNT(*)
FROM LoungePostScrap postScrap
         JOIN LoungePost post
              ON post.loungePostId = postScrap.loungePostId
WHERE post.title LIKE 'DU102-PERF-%'

UNION ALL

SELECT
    'LoungeCommentLike',
    COUNT(*)
FROM LoungeCommentLike commentLike
         JOIN LoungeComment comment
              ON comment.loungeCommentId = commentLike.loungeCommentId
WHERE comment.content LIKE 'DU102-PERF-%';