-- V20260810000200에서 role 컬럼을 추가할 때, isLeader = 1인 행만 LEAD_ARTIST로 채우고
-- 나머지는 기본값 CO_AUTHOR로 두었다. 그 결과 "작가는 아니지만 QnA 담당자로 지정된 전시 대표자"
-- 행까지 공동 작업자로 분류되어, 작품 상세의 coAuthors에 노출되고 수정 화면에도 프리필된다.
-- 그 상태로 저장하면 requireVerifiedArtistTeamMember 검증에 걸려 수정 자체가 실패한다.
--
-- 데이터만으로는 "QnA 담당자인 공동 작업자"와 "QnA 담당 전용 대표자"가 같은 모양이라
-- (role=CO_AUTHOR, isContact=1, userId 존재) 일반적으로는 구분할 수 없다.
-- 다만 AuthorSetupService는 담당자가 작가 그룹(artistUserIds)에 없을 때만 QA_ONLY 행을 만들므로,
-- 아래 조건이면 등록 시점에 작가 그룹이 비어 있었음이 확정된다.
--   1) 대표 작가의 userId가 NULL      -> 계정 없는 작가를 대리 등록
--   2) 해당 작품에 다른 공동 작업자 0명 -> 계정이 연결된 공동 작업자도 없음
--   3) 대상자가 전시 소유자 또는 팀장이고 isContact = 1
-- 조건을 만족하지 않는 행은 진짜 공동 작업자일 수 있으므로 건드리지 않는다.
--
-- MySQL은 UPDATE 대상 테이블을 서브쿼리에서 참조할 수 없다(ERROR 1093).
-- 파생 테이블도 Creator를 다시 읽으면 같은 제약에 걸리므로, 대상 creatorId를 임시 테이블에
-- 먼저 확정한 뒤 조인해 갱신한다.
CREATE TEMPORARY TABLE creator_qa_only_target
(
    creatorId BIGINT NOT NULL PRIMARY KEY
);

INSERT INTO creator_qa_only_target (creatorId)
SELECT c.creatorId
FROM Creator c
         JOIN DisplayArtwork a ON a.displayArtworkId = c.displayArtworkId
         JOIN Display d ON d.displayId = a.displayId
         LEFT JOIN TeamMember tm ON tm.displayId = a.displayId
    AND tm.userId = c.userId
    AND tm.deletedAt IS NULL
         LEFT JOIN Creator la ON la.displayArtworkId = c.displayArtworkId
    AND la.isLeader = 1
         LEFT JOIN Creator other ON other.displayArtworkId = c.displayArtworkId
    AND other.creatorId <> c.creatorId
    AND other.isLeader = 0
WHERE c.role = 'CO_AUTHOR'
  AND c.isContact = 1
  AND c.userId IS NOT NULL
  AND (d.userId = c.userId OR tm.author = 'TEAM_LEADER')
  AND la.userId IS NULL
  AND other.creatorId IS NULL;

UPDATE Creator c
    JOIN creator_qa_only_target t ON t.creatorId = c.creatorId
SET c.role = 'QA_ONLY';

DROP TEMPORARY TABLE creator_qa_only_target;
