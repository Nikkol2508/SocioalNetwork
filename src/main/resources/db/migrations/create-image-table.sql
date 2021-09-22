--liquibase formatted sql
--changeset nikolay:create-image-table

CREATE TABLE IF NOT EXISTS image
(
    id       SERIAL NOT NULL,
    owner_id INT    NOT NULL,
    name     TEXT   NOT NULL,
    path     TEXT   NOT NULL,
    url      TEXT,
    format   TEXT   NOT NULL,
    bytes    BIGINT NOT NULL,
    type     TEXT   NOT NULL,
    time     BIGINT NOT NULL,
    data     BYTEA  NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (owner_id) REFERENCES person (id) ON DELETE CASCADE
);
