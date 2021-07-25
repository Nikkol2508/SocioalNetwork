--liquibase formatted sql
--changeset ivoligo:create-tags

insert into tag (tag)
values ('Java'),
       ('JavaScript'),
       ('Bug'),
       ('Fix'),
       ('HTML'),
       ('Oracle'),
       ('Spring'),
       ('Youtube'),
       ('beer'),
       ('brain'),
       ('Mayday');