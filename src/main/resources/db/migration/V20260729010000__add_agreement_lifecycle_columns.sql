ALTER TABLE Agreement
    ADD COLUMN code VARCHAR(100) NULL AFTER agreeId,
    ADD COLUMN version VARCHAR(32) NULL AFTER isRequired,
    ADD COLUMN isActive TINYINT(1) NULL AFTER version,
    ADD COLUMN effectiveDate DATE NULL AFTER isActive,
    ADD COLUMN displayOrder INT NULL AFTER effectiveDate;

UPDATE Agreement
SET code = CASE
        WHEN title IN ('서비스 이용약관 v1', '서비스 이용약관 v2', '서비스 이용약관')
            AND type = 'SERVICE'
            THEN 'TERMS_OF_SERVICE'
        WHEN title IN ('개인정보 처리방침 v1', '개인정보 처리방침 v2', '개인정보 처리방침')
            AND type = 'PRIVACY'
            THEN 'PRIVACY_COLLECTION_USE'
        WHEN title IN ('마케팅 정보 수신 동의 v1', '마케팅 정보 수신 동의 v2', '마케팅 정보 수신 동의')
            AND type = 'MARKETING'
            THEN 'MARKETING_CONSENT'
        WHEN title = '위치 기반 서비스 약관'
            AND type = 'SERVICE'
            THEN 'LOCATION_BASED_SERVICE'
        WHEN title = '작품 공개 정책 안내'
            AND type = 'SERVICE'
            THEN 'ARTWORK_PUBLICATION_POLICY'
        WHEN title = '이용약관 이용 안내'
            AND type = 'SERVICE'
            THEN 'TERMS_USAGE_GUIDE'
        WHEN title = '이벤트 알림 수신 동의'
            AND type = 'MARKETING'
            THEN 'EVENT_NOTIFICATION_CONSENT'
        ELSE CONCAT('LEGACY_AGREEMENT_', agreeId)
    END,
    version = CASE
        WHEN title IN ('서비스 이용약관 v1', '개인정보 처리방침 v1', '마케팅 정보 수신 동의 v1')
            THEN '1.0'
        WHEN title IN ('서비스 이용약관 v2', '개인정보 처리방침 v2', '마케팅 정보 수신 동의 v2')
            THEN '2.0'
        ELSE CONCAT('LEGACY-', agreeId)
    END,
    isActive = 1,
    effectiveDate = DATE(createdAt),
    displayOrder = agreeId;

CREATE INDEX idx_agreement_code_version
    ON Agreement (code, version);

CREATE INDEX idx_agreement_active_effective_order
    ON Agreement (isActive, effectiveDate, displayOrder);

CREATE INDEX idx_agreement_active_required_effective
    ON Agreement (isActive, isRequired, effectiveDate);
