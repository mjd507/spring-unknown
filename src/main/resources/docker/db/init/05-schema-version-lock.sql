CREATE TABLE payment
(
    id         BIGINT         NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pmt_id     VARCHAR(32)    NOT NULL,
    amount     DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version    INT            NOT NULL,
    CONSTRAINT pmt_id_unique UNIQUE (pmt_id)
);
