-- 대상마다 한 번만 보내는 알림(저장한 작가의 새 전시, 저장한 전시 시작)을 저장하기 전에
-- "이 전시로 이미 받은 사람"을 찾는 조회용 인덱스
CREATE INDEX `IDX_NOTIFICATION_TARGET_TYPE` ON `Notification` (`targetId`, `type`);
