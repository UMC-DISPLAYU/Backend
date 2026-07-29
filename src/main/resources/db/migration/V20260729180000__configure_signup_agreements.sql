UPDATE Agreement
SET code = CONCAT('LEGACY_', code, '_', agreeId),
    isActive = 0
WHERE title IN (
        '서비스 이용약관 v1',
        '서비스 이용약관 v2',
        '개인정보 처리방침 v1',
        '개인정보 처리방침 v2'
    );

UPDATE Agreement
SET code = 'TERMS_OF_SERVICE',
    title = '서비스 이용약관',
    type = 'SERVICE',
    content = '서비스 목적과 용어 정의, 회원가입 및 계정 관리, 서비스 제공 범위, 전시 대표자 및 팀원의 권한, 회원의 의무, 게시물과 저작권, 서비스 변경 및 중단, 이용 제한, 회원 탈퇴 및 데이터 미반영, 책임 제한, 분쟁 해결 및 준거법에 관한 약관입니다.',
    isRequired = 1,
    version = '1.0',
    isActive = 1,
    effectiveDate = '2026-08-01',
    displayOrder = 1
WHERE title = '서비스 이용약관'
  AND type = 'SERVICE';

UPDATE Agreement
SET code = 'PRIVACY_COLLECTION_USE',
    title = '개인정보 수집·이용 동의',
    type = 'PRIVACY',
    content = '회원 식별, 계정 관리 및 서비스 제공을 위해 소셜로그인 식별값, 이메일, 닉네임, 프로필 이미지와 서비스 활동 정보를 수집·이용합니다. 개인정보는 회원 탈퇴 시까지 보유하며 법령상 보관이 필요한 경우 해당 기간 동안 별도 보관합니다. 필수 정보 수집·이용에 동의하지 않으면 회원가입이 제한됩니다.',
    isRequired = 1,
    version = '1.0',
    isActive = 1,
    effectiveDate = '2026-08-01',
    displayOrder = 2
WHERE title = '개인정보 처리방침'
  AND type = 'PRIVACY';

UPDATE Agreement
SET code = 'LOCATION_BASED_SERVICE',
    title = '위치기반서비스 이용약관',
    type = 'SERVICE',
    content = '현재 위치를 기준으로 가까운 전시를 추천하고 전시장까지의 거리를 표시하기 위한 선택 약관입니다. 위치 기능을 사용할 때만 현재 위치를 이용하며 지속적인 위치 추적이나 이동 경로 수집을 하지 않습니다. 위치정보는 추천과 거리 계산 완료 후 파기하며 동의하지 않아도 일반 전시 검색과 서비스 이용이 가능합니다.',
    isRequired = 0,
    version = '1.0',
    isActive = 1,
    effectiveDate = '2026-08-01',
    displayOrder = 3
WHERE title = '위치 기반 서비스 약관'
  AND type = 'SERVICE';
