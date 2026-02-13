-- 같은 유저가 같은 날 2회 이상 참여(cancelled=false)하는 것을 DB 레벨에서 방지
CREATE UNIQUE INDEX idx_spin_daily_unique
ON spin_history (user_id, CAST(created_at AS DATE))
WHERE cancelled = false;
