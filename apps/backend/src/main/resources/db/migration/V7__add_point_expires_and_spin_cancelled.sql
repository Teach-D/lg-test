-- point_history 테이블에 유효기간 컬럼 추가
ALTER TABLE point_history ADD COLUMN expires_at TIMESTAMP NULL;

-- 만료일 조회 성능 인덱스
CREATE INDEX idx_point_history_expires_at ON point_history(expires_at)
    WHERE expires_at IS NOT NULL;

-- spin_history 테이블에 취소 여부 컬럼 추가
ALTER TABLE spin_history ADD COLUMN cancelled BOOLEAN NOT NULL DEFAULT FALSE;
