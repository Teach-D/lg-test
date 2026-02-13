CREATE TABLE point_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount INT NOT NULL,
    type VARCHAR(30) NOT NULL,
    description VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_point_history_user_id ON point_history(user_id);
CREATE INDEX idx_point_history_created_at ON point_history(created_at DESC);

COMMENT ON TABLE point_history IS '포인트 이력';
COMMENT ON COLUMN point_history.user_id IS '사용자 ID';
COMMENT ON COLUMN point_history.amount IS '포인트 변동량 (양수=적립, 음수=차감)';
COMMENT ON COLUMN point_history.type IS '포인트 유형 (SIGNUP_BONUS, ROULETTE_WIN, PRODUCT_EXCHANGE, ADMIN_ADJUST)';
COMMENT ON COLUMN point_history.description IS '변동 설명';
