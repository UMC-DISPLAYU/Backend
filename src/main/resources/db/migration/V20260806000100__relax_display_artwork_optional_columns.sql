-- 작품설명/규격/감상 포인트는 디자인상 선택 항목이므로 NULL을 허용한다.
-- 개인 작품(PersonalArtwork)의 동일 컬럼과 형태를 맞춘다.
ALTER TABLE DisplayArtwork
    MODIFY content TEXT NULL,
    MODIFY size VARCHAR(255) NULL,
    MODIFY point TEXT NULL;
