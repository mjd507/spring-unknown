DROP TABLE IF EXISTS people;

CREATE TABLE employee  (
    person_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);