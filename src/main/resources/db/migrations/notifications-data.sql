--liquibase formatted sql
--changeset andrei:notifications-data

INSERT INTO notification_type (code, name)
VALUES ('FRIEND_REQUEST', 'друзья'),
       ('FRIEND_REQUEST', 'друзья');

INSERT INTO notification (type_id, send_time, person_id, entity_id, contact)
VALUES (1, 1625127990000, 12, 4, 'miominji@mail.ru'),
       (2, 1625127990000, 12, 5, 'miominji@mail.ru');