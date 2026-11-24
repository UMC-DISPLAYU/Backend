-- KDI국제정책대학원대학교의 서비스 지원 여부는 PM 승인 후 배포한다.
INSERT INTO `University` (`name`, `active`)
SELECT schools.universityName, 1
FROM (
    SELECT '백석예술대학교' AS universityName
    UNION ALL SELECT 'KDI국제정책대학원대학교'
) schools
WHERE NOT EXISTS (
    SELECT 1 FROM `University` u WHERE u.`name` = schools.universityName
);

INSERT INTO `UniversityEmailDomain` (`universityId`, `domain`)
SELECT u.`universityId`, domains.emailDomain
FROM (
    SELECT '백석예술대학교' AS universityName, 'bau.ac.kr' AS emailDomain
    UNION ALL SELECT 'KDI국제정책대학원대학교', 'kdis.ac.kr'
) domains
JOIN `University` u ON u.`name` = domains.universityName
WHERE NOT EXISTS (
    SELECT 1 FROM `UniversityEmailDomain` d
    WHERE d.`universityId` = u.`universityId` AND d.`domain` = domains.emailDomain
);
