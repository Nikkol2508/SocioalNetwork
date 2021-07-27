--liquibase formatted sql
--changeset nikolai:data-person-user

ALTER TABLE person DROP COLUMN reg_date, DROP COLUMN birth_date, DROP COLUMN last_online_time;

ALTER TABLE person ADD reg_date BIGINT NOT NULL, ADD birth_date BIGINT, ADD last_online_time BIGINT;

ALTER TABLE person ALTER COLUMN phone DROP NOT NULL;

INSERT INTO person (first_name,last_name,e_mail,phone,"password",about,town,is_approved,reg_date,birth_date,last_online_time)
VALUES ('Вася', 'Васичкин','vasy@yandex.ru','89998887744','12345','Я Вася','Россия Москва',true,1625127990000,964513590000,1627200965049),
       ('Хома', 'Хомяков', 'homa@yandex.ru','89998887744','12345','Я Хомяков','Россия Москва',true,1625127990000,806660790000,1627200965049),
       ('Иван','Иванов','ivan@yandex.ru','89998887744','12345','Немного обо мне','Россия Москва',true,1625127990000,901355190000,1627200965049),
       ('Пётр','Петров','petr@yandex.ru','89998887744','12345','Немного обо мне','Россия Омск',true,1625127990000,901355190000,1627200965049),
       ('Сергей','Семёнов','cergei@yandex.ru','89998887744','12345','Немного обо мне','Россия Уфа',true,1625127990000,901355190000,1627200965049),
       ('Николай','Аксёнов','nik@yandex.ru','89998887744','12345','Немного обо мне','Россия Ногинск',true,1625127990000,207131190000,1627200965049),
       ('Дминрий','Скороход','dmitriy@yandex.ru','89998887744','12345','Немного обо мне','Россия Тагил',true,1625127990000,901355190000,1627200965049),
       ('Илья','Карапузов','ilia@yandex.ru','89998887744','12345','Немного обо мне','Россия Керчь',true,1625127990000,901355190000,1627200965049),
       ('Данила','Крюков','danila@yandex.ru','89998887744','12345','Немного обо мне','Россия Воронеж',true,1625127990000,901355190000,1627200965049),
       ('Роберт','Карасян','robert@yandex.ru','89998887744','12345','Немного обо мне','Россия Талдом',false,1625127990000,901355190000,1627200965049);

INSERT INTO "user" (name, e_mail, password, type)
VALUES ('Вася', 'vasy@yandex.ru', '12345', 'MODERATOR'),
       ('Петя', 'petya@test.ru', 'password2', 'MODERATOR'),
       ('Андрей', 'andrey@test.ru', 'password3', 'ADMIN'),
       ('Сергей', 'cergei@yandex.ru', '12345', 'ADMIN'),
       ('Кира', 'kira@test.ru', 'password5', 'MODERATOR');