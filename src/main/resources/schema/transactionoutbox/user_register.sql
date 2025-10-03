DROP TABLE user_register IF EXISTS;

CREATE TABLE user_register  (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30),
    email VARCHAR(50),
    create_date DATE
);

