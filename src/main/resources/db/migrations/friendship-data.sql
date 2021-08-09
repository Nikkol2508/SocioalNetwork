--liquibase formatted sql
--changeset andrei:friendship-data

INSERT INTO friendship_status(time, name, code)
VALUES (1625127990000, 'Друзья', 'FRIEND'),
       (1625127990000, 'Друзья', 'FRIEND'),
       (1625127990000, 'Друзья', 'FRIEND'),
       (1625127990000, 'Друзья', 'FRIEND'),
       (1625127990000, 'Друзья', 'FRIEND');

INSERT INTO friendship(status_id, src_person_id, dst_person_id)
VALUES (1, 2, 3),
       (2, 2, 6),
       (3, 2, 8),
       (4, 2, 1),
       (5, 2, 4);


