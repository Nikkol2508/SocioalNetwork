--liquibase formatted sql
--changeset andrei:notifications-settings-data

INSERT INTO notification_setting_type (code, status)
VALUES ('POST', DEFAULT),
       ('POST_COMMENT', DEFAULT),
       ('COMMENT_COMMENT', DEFAULT),
       ('FRIEND_REQUEST', DEFAULT),
       ('MESSAGE', DEFAULT),
       ('FRIEND_BIRTHDAY', DEFAULT);

INSERT INTO notification_settings (person_id, type_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (1, 6),
       (2, 1),
       (2, 2),
       (2, 3),
       (2, 4),
       (2, 5),
       (2, 6),
       (3, 1),
       (3, 2),
       (3, 3),
       (3, 4),
       (3, 5),
       (3, 6),
       (4, 1),
       (4, 2),
       (4, 3),
       (4, 4),
       (4, 5),
       (4, 6);
