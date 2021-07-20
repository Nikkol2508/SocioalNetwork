--liquibase formatted sql
--changeset vitaly:create-all-tables

CREATE TYPE user_type as ENUM ('MODERATOR', 'ADMIN');
CREATE TYPE permission_type as ENUM ('ALL', 'FRIENDS');
CREATE TYPE status_type as ENUM ('REQUEST', 'FRIEND', 'BLOCKED', 'DECLINED', 'SUBSCRIBED');
CREATE TYPE action_type as ENUM ('BLOCK', 'UNBLOCK');
CREATE TYPE message_status_type as ENUM ('SENT', 'READ');

CREATE TABLE IF NOT EXISTS "user"
(
    id       SERIAL    NOT NULL,
    name     TEXT      NOT NULL,
    e_mail   TEXT      NOT NULL,
    password TEXT      NOT NULL,
    type     user_type NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS person
(
    Id                  SERIAL    NOT NULL,
    first_name          TEXT      NOT NULL,
    last_name           TEXT      NOT NULL,
    reg_date            TIMESTAMP NOT NULL,
    birth_date          DATE,
    e_mail              TEXT      NOT NULL,
    phone               TEXT      NOT NULL,
    password            TEXT      NOT NULL,
    photo               TEXT,
    about               TEXT,
    town                TEXT,
    confirmation_code   TEXT,
    is_approved         BOOLEAN,
    messages_permission permission_type,
    last_online_time    TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS post
(
    id         SERIAL    NOT NULL,
    time       TIMESTAMP NOT NULL,
    author_id  INT       NOT NULL,
    tittle     TEXT      NOT NULL,
    post_text  TEXT      NOT NULL,
    is_blocked BOOLEAN   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (author_id) REFERENCES person (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS friendship_status
(
    id   SERIAL    NOT NULL,
    time TIMESTAMP NOT NULL,
    name TEXT      NOT NULL,
    code status_type,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS notification_type
(
    id   SERIAL NOT NULL,
    code TEXT   NOT NULL,
    name TEXT   NOT NULL,
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
    id           SERIAL    NOT NULL,
    time         TIMESTAMP NOT NULL,
    post_id      INT       NOT NULL,
    parent_id    INT,
    author_id    INT       NOT NULL,
    comment_text TEXT      NOT NULL,
    is_blocked   BOOLEAN   NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE RESTRICT,
    FOREIGN KEY (parent_id) REFERENCES post_comment (id) ON DELETE RESTRICT,
    FOREIGN KEY (author_id) REFERENCES person (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS block_history
(
    id         SERIAL    NOT NULL,
    time       TIMESTAMP NOT NULL,
    person_id  INT       NOT NULL,
    post_id    INT       NOT NULL,
    comment_id INT       NOT NULL,
    action     action_type,
    PRIMARY KEY (id),
    FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE RESTRICT,
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE RESTRICT,
    FOREIGN KEY (comment_id) REFERENCES post_comment (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS message
(
    id           SERIAL    NOT NULL,
    time         TIMESTAMP NOT NULL,
    author_id    INT       NOT NULL,
    recipient_id INT       NOT NULL,
    message_text TEXT,
    read_status  message_status_type,
    PRIMARY KEY (id),
    FOREIGN KEY (author_id) REFERENCES person (id) ON DELETE RESTRICT,
    FOREIGN KEY (recipient_id) REFERENCES person (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS friendship
(
    id            SERIAL NOT NULL,
    status_id     INT    NOT NULL,
    src_person_id INT    NOT NULL,
    dst_person_id INT    NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (status_id) REFERENCES friendship_status (id) ON DELETE RESTRICT,
    FOREIGN KEY (src_person_id) REFERENCES person (id) ON DELETE RESTRICT,
    FOREIGN KEY (dst_person_id) REFERENCES person (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS post_like
(
    id        SERIAL    NOT NULL,
    time      TIMESTAMP NOT NULL,
    person_id INT       NOT NULL,
    post_id   INT       NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE RESTRICT,
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS post_file
(
    id      SERIAL NOT NULL,
    post_id INT    NOT NULL,
    name    TEXT,
    path    TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS post2tag
(
    id      SERIAL NOT NULL,
    post_id INT    NOT NULL,
    tag_id  INT    NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE RESTRICT,
    FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS notification
(
    id        SERIAL    NOT NULL,
    type_id   INT       NOT NULL,
    send_time TIMESTAMP NOT NULL,
    person_id INT       NOT NULL,
    entity_id INT       NOT NULL,
    contact   TEXT      NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (person_id) REFERENCES person (id) ON DELETE RESTRICT,
    FOREIGN KEY (type_id) REFERENCES notification_type (id) ON DELETE RESTRICT
);