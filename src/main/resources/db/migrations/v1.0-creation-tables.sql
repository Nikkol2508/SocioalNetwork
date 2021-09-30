--liquibase formatted sql
--changeset vitaly:create-all-tables

CREATE TABLE IF NOT EXISTS "user"
(
    id       SERIAL NOT NULL,
    name     TEXT   NOT NULL,
    e_mail   TEXT   NOT NULL,
    password TEXT   NOT NULL,
    type     TEXT   NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS person
(
    Id                  SERIAL NOT NULL,
    first_name          TEXT   NOT NULL,
    last_name           TEXT   NOT NULL,
    reg_date            BIGINT NOT NULL,
    birth_date          BIGINT,
    e_mail              TEXT   NOT NULL UNIQUE,
    phone               TEXT,
    password            TEXT   NOT NULL,
    photo               TEXT,
    about               TEXT,
    country             TEXT,
    city                TEXT,
    confirmation_code   TEXT,
    is_approved         BOOLEAN,
    messages_permission TEXT   NOT NULL,
    last_online_time    BIGINT,
    is_blocked          BOOLEAN,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS post
(
    id         SERIAL  NOT NULL,
    time       BIGINT  NOT NULL,
    author_id  INT     NOT NULL,
    title      TEXT    NOT NULL,
    post_text  TEXT    NOT NULL,
    is_blocked BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (author_id) REFERENCES person (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friendship_status
(
    id   SERIAL NOT NULL,
    time BIGINT NOT NULL,
    name TEXT   NOT NULL,
    code TEXT   NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tag
(
    id  SERIAL NOT NULL,
    tag TEXT   NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS post_comment
(
    id           SERIAL  NOT NULL,
    time         BIGINT  NOT NULL,
    post_id      INT     NOT NULL,
    parent_id    INT,
    author_id    INT     NOT NULL,
    comment_text TEXT    NOT NULL,
    is_blocked   BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES post_comment (id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES person (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS block_history
(
    id         SERIAL NOT NULL,
    time       BIGINT NOT NULL,
    person_id  INT    NOT NULL,
    post_id    INT    NOT NULL,
    comment_id INT    NOT NULL,
    action     TEXT   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES post_comment (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS message
(
    id           SERIAL NOT NULL,
    time         BIGINT NOT NULL,
    author_id    INT    NOT NULL,
    recipient_id INT    NOT NULL,
    message_text TEXT,
    read_status  TEXT   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (author_id) REFERENCES person (id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES person (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friendship
(
    id            SERIAL NOT NULL,
    status_id     INT    NOT NULL,
    src_person_id INT    NOT NULL,
    dst_person_id INT    NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (status_id) REFERENCES friendship_status (id) ON DELETE CASCADE,
    FOREIGN KEY (src_person_id) REFERENCES person (id) ON DELETE CASCADE,
    FOREIGN KEY (dst_person_id) REFERENCES person (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_like
(
    id        SERIAL NOT NULL,
    time      BIGINT NOT NULL,
    person_id INT    NOT NULL,
    item_id   INT    NOT NULL,
    type      TEXT   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_file
(
    id      SERIAL NOT NULL,
    post_id INT    NOT NULL,
    name    TEXT,
    path    TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post2tag
(
    id      SERIAL NOT NULL,
    post_id INT    NOT NULL,
    tag_id  INT    NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notification
(
    id            SERIAL NOT NULL,
    send_time     BIGINT NOT NULL,
    person_id     INT    NOT NULL,
    entity_id     INT    NOT NULL,
    contact       TEXT   NOT NULL,
    src_person_id INT    NOT NULL,
    type          TEXT   NOT NULL,
    name          TEXT   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE CASCADE,
    FOREIGN KEY (src_person_id) REFERENCES person (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notification_setting_type
(
    id     SERIAL  NOT NULL,
    code   TEXT    NOT NULL,
    status BOOLEAN NOT NULL DEFAULT true,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS notification_settings
(
    id        SERIAL NOT NULL,
    person_id INT    NOT NULL,
    type_id   INT    NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE CASCADE,
    FOREIGN KEY (type_id) REFERENCES notification_setting_type (id) ON DELETE CASCADE
);