CREATE TABLE roulette_segment (
  id            BIGSERIAL    PRIMARY KEY,
  label         VARCHAR(50)  NOT NULL,
  reward_point  INTEGER      NOT NULL DEFAULT 0,
  weight        INTEGER      NOT NULL DEFAULT 1,
  display_order INTEGER      NOT NULL DEFAULT 0,
  created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
  updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE spin_history (
  id             BIGSERIAL    PRIMARY KEY,
  user_id        BIGINT       NOT NULL,
  segment_id     BIGINT       NOT NULL,
  segment_label  VARCHAR(50)  NOT NULL,
  reward_point   INTEGER      NOT NULL,
  cost_point     INTEGER      NOT NULL,
  created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
  updated_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_spin_history_user_id ON spin_history (user_id);

-- 기본 룰렛 세그먼트 데이터
INSERT INTO roulette_segment (label, reward_point, weight, display_order) VALUES
  ('100P', 100, 30, 1),
  ('200P', 200, 25, 2),
  ('꽝', 0, 20, 3),
  ('500P', 500, 10, 4),
  ('50P', 50, 35, 5),
  ('꽝', 0, 20, 6),
  ('1000P', 1000, 5, 7),
  ('300P', 300, 15, 8);