DROP TABLE outbox_event IF EXISTS;

CREATE TABLE outbox_event  (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(30),
    event_body VARCHAR(1024),
    event_date DATE,
    complete_date DATE
);

