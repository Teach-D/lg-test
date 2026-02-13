CREATE TABLE orders (
  id           BIGSERIAL    PRIMARY KEY,
  user_id      BIGINT       NOT NULL,
  product_id   BIGINT       NOT NULL,
  product_name VARCHAR(100) NOT NULL,
  point_cost   INTEGER      NOT NULL,
  status       VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
  order_type   VARCHAR(20)  NOT NULL DEFAULT 'EXCHANGE',
  created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
  updated_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);