--liquibase formatted sql
--changeset nikolai:data-person-user

INSERT INTO person (first_name, last_name, e_mail, phone, password, about, country, city, is_approved, is_blocked,
                    messages_permission, reg_date, birth_date, last_online_time)
VALUES ('Вася', 'Васичкин', 'vasy@yandex.ru', '89998887744',
        '$2a$12$RvaD2mYu8Mv7EUD66PiGNeY8gtbgX9L2IVRs.AUzx1w8o3DGKz4RO', 'Я Вася', 'Россия', 'Москва', true, false,
        'ALL', 1625127990000, 964513590000, 1627200965049),
       ('Хома', 'Хомяков', 'homa@yandex.ru', '89998887744',
        '$2a$12$RvaD2mYu8Mv7EUD66PiGNeY8gtbgX9L2IVRs.AUzx1w8o3DGKz4RO', 'Я Хомяков', 'Россия', 'Москва', true, false,
        'ALL', 1625127990000, 806660790000, 1627200965049),
       ('Иван', 'Иванов', 'ivan@yandex.ru', '89998887744',
        '$2a$12$RvaD2mYu8Mv7EUD66PiGNeY8gtbgX9L2IVRs.AUzx1w8o3DGKz4RO', 'Немного обо мне', 'Россия', 'Москва', true,
        false, 'ALL', 1625127990000, 901355190000, 1627200965049),
       ('Пётр', 'Петров', 'petr@yandex.ru', '89998887744',
        '$2a$12$RvaD2mYu8Mv7EUD66PiGNeY8gtbgX9L2IVRs.AUzx1w8o3DGKz4RO', 'Немного обо мне', 'Россия', 'Омск', true,
        false,
        'ALL', 1625127990000, 901355190000, 1627200965049),
       ('Сергей', 'Семёнов', 'cergei@yandex.ru', '89998887744',
        '$2a$12$RvaD2mYu8Mv7EUD66PiGNeY8gtbgX9L2IVRs.AUzx1w8o3DGKz4RO', 'Немного обо мне', 'Россия', 'Уфа', true,
        false, 'ALL', 1625127990000, 901355190000, 1627200965049),
       ('Николай', 'Аксёнов', 'nik@yandex.ru', '89998887744',
        '$2a$12$RvaD2mYu8Mv7EUD66PiGNeY8gtbgX9L2IVRs.AUzx1w8o3DGKz4RO', 'Немного обо мне', 'Россия', 'Ногинск', true,
        false, 'ALL', 1625127990000, 207131190000, 1627200965049),
       ('Дминрий', 'Скороход', 'dmitriy@yandex.ru', '89998887744',
        '$2a$12$RvaD2mYu8Mv7EUD66PiGNeY8gtbgX9L2IVRs.AUzx1w8o3DGKz4RO', 'Немного обо мне', 'Россия', 'Тагил',
        true, false, 'ALL', 1625127990000, 901355190000, 1627200965049),
       ('Илья', 'Карапузов', 'ilia@yandex.ru', '89998887744',
        '$2a$12$RvaD2mYu8Mv7EUD66PiGNeY8gtbgX9L2IVRs.AUzx1w8o3DGKz4RO', 'Немного обо мне', 'Россия', 'Керчь', true,
        false, 'ALL', 1625127990000, 901355190000, 1627200965049),
       ('Данила', 'Крюков', 'danila@yandex.ru', '89998887744',
        '$2a$12$RvaD2mYu8Mv7EUD66PiGNeY8gtbgX9L2IVRs.AUzx1w8o3DGKz4RO', 'Немного обо мне', 'Россия', 'Воронеж', true,
        false, 'ALL', 1625127990000, 901355190000, 1627200965049),
       ('Роберт', 'Карасян', 'robert@yandex.ru', '89998887744',
        '$2a$12$RvaD2mYu8Mv7EUD66PiGNeY8gtbgX9L2IVRs.AUzx1w8o3DGKz4RO', 'Немного обо мне', 'Россия', 'Талдом',
        false, false, 'ALL', 1625127990000, 901355190000, 1627200965049);

INSERT INTO "user" (name, e_mail, password, type)
VALUES ('Вася', 'vasy@yandex.ru', '12345678', 'MODERATOR'),
       ('Петя', 'petya@test.ru', 'password2', 'MODERATOR'),
       ('Андрей', 'andrey@test.ru', 'password3', 'ADMIN'),
       ('Сергей', 'cergei@yandex.ru', '12345678', 'ADMIN'),
       ('Кира', 'kira@test.ru', 'password5', 'MODERATOR');