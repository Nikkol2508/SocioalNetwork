--liquibase formatted sql
--changeset ivoligo:created-user

insert into public."user" (name, e_mail, password, type)
values ('Вася', 'vasya@test.ru', 'password1', 'MODERATOR'),
       ('Петя', 'petya@test.ru', 'password2', 'MODERATOR'),
       ('Андрей', 'andrey@test.ru', 'password3', 'ADMIN'),
       ('Сергей', 'sergey@test.ru', 'password4', 'ADMIN'),
       ('Кира', 'kira@test.ru', 'password5', 'MODERATOR');
