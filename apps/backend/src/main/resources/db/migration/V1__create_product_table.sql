CREATE TABLE product (
  id         BIGSERIAL    PRIMARY KEY,
  name       VARCHAR(100) NOT NULL,
  image_url  VARCHAR(500),
  point_cost INTEGER      NOT NULL CHECK (point_cost >= 0),
  stock      INTEGER      NOT NULL CHECK (stock >= 0),
  active     BOOLEAN      NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);
