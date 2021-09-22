--liquibase formatted sql
--changeset andrei:create-country-data

CREATE TABLE IF NOT EXISTS country
(
    id   SERIAL NOT NULL,
    name TEXT   NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO country (name)
VALUES ('Россия'),
       ('США');