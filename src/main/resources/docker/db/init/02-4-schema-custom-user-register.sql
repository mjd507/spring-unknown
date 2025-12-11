DROP TABLE IF EXISTS user_register;

CREATE TABLE user_register  (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(30),
    email VARCHAR(50),
    create_date TIMESTAMP
);

