CREATE TABLE system_user
(
    id         BIGINT       NOT NULL,
    name       VARCHAR(255) NOT NULL,
    birth_date DATE,
    cellphone  VARCHAR(20),
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_system_user PRIMARY KEY (id),
    CONSTRAINT uq_system_user_email UNIQUE (email)
);

CREATE SEQUENCE system_user_seq
    START WITH 1
    INCREMENT BY 50;