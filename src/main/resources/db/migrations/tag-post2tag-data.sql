--liquibase formatted sql
--changeset ivoligo:create-tags-post2tag

INSERT INTO tag (tag)
VALUES ('Java'),
       ('JavaScript'),
       ('Bug'),
       ('Fix'),
       ('HTML'),
       ('SQL'),
       ('Spring'),
       ('Youtube'),
       ('Hibernate'),
       ('Тестирование'),
       ('beer');

INSERT INTO post2tag (post_id, tag_id)
VALUES (1, 11),
       (2, 1),
       (2, 7),
       (3, 5),
       (3, 11),
       (4, 3),
       (5, 1),
       (5, 2),
       (6, 8),
       (7, 6),
       (8, 6),
       (9, 9),
       (10, 11),
       (12, 2),
       (13, 4),
       (15, 3),
       (15, 4);