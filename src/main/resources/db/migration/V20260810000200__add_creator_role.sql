-- Creator는 지금까지 isLeader(대표 작가 여부)로만 구분되어 있었다.
-- 그래서 isLeader = 0인 행에 "공동 작업자"와 "작가가 아닌 QnA 담당 전용 대표자"가 함께 섞여 있었고,
-- 조회 시점에는 둘을 구분할 근거가 없었다. 역할을 명시적으로 남기기 위해 role 컬럼을 추가한다.
ALTER TABLE Creator
    ADD COLUMN role ENUM ('LEAD_ARTIST', 'CO_AUTHOR', 'QA_ONLY') NOT NULL DEFAULT 'CO_AUTHOR'
        COMMENT 'LEAD_ARTIST(대표 작가) / CO_AUTHOR(공동 작업자) / QA_ONLY(작가가 아닌 QnA 담당자)'
        AFTER isLeader;

-- 기존 데이터 백필.
-- 대표 작가만 isLeader로 복원할 수 있고, 나머지는 공동 작업자와 QnA 담당 전용을 구분할 근거가 남아 있지 않아
-- 기본값인 CO_AUTHOR를 그대로 둔다. 해당 작품을 한 번 수정 저장하면 올바른 role로 다시 기록된다.
UPDATE Creator
SET role = 'LEAD_ARTIST'
WHERE isLeader = 1;
