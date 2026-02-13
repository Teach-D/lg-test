CREATE TABLE budget (
  id           BIGSERIAL    PRIMARY KEY,
  period_type  VARCHAR(10)  NOT NULL,
  period_date  DATE         NOT NULL,
  limit_amount BIGINT       NOT NULL CHECK (limit_amount >= 0),
  spent_amount BIGINT       NOT NULL DEFAULT 0,
  created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
  updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
  UNIQUE (period_type, period_date)
);
