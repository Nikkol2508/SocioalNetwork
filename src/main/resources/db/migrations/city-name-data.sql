--liquibase formatted sql
--changeset andrei:city-create-data

CREATE TABLE IF NOT EXISTS city
(
    id   SERIAL NOT NULL,
    name TEXT   NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO city (name)
VALUES ('Москва'),
       ('Омск'),
       ('Уфа'),
       ('Ногинск'),
       ('Тагил'),
       ('Керчь'),
       ('Воронеж'),
       ('Талдом');