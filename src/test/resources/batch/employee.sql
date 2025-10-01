DROP TABLE people IF EXISTS;

CREATE TABLE employee  (
    person_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);