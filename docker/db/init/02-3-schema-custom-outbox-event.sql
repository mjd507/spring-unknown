DROP TABLE IF EXISTS outbox_event;

CREATE TABLE outbox_event  (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_type VARCHAR(30),
    event_body VARCHAR(1024),
    event_date TIMESTAMP,
    complete_date TIMESTAMP
);

