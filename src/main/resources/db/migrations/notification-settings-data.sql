--liquibase formatted sql
--changeset andrei:notifications-settings-data

INSERT INTO notification_setting_type (code, status)
VALUES ('POST', DEFAULT),
       ('POST_COMMENT', DEFAULT),
       ('COMMENT_COMMENT', DEFAULT),
       ('FRIEND_REQUEST', DEFAULT),
       ('MESSAGE', DEFAULT),
       ('FRIEND_BIRTHDAY', DEFAULT);