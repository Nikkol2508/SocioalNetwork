

INSERT INTO person (first_name, last_name, e_mail, phone, password, about, country, city, is_approved, is_blocked,
                    messages_permission, reg_date, birth_date, last_online_time)
VALUES ('Вася', 'Васичкин', 'test@yandex.ru', '89998887744',
        '$2a$12$RvaD2mYu8Mv7EUD66PiGNeY8gtbgX9L2IVRs.AUzx1w8o3DGKz4RO', 'Я Вася', 'Россия', 'Москва', true, false,
        'ALL', 1625127990000, 964513590000, 1627200965049);

INSERT INTO person (first_name, last_name, e_mail, password, is_approved, is_blocked,
                    messages_permission, reg_date, last_online_time)
VALUES ('Вася', 'Васичкин', 'test2@yandex.ru',
        '$2a$12$RvaD2mYu8Mv7EUD66PiGNeY8gtbgX9L2IVRs.AUzx1w8o3DGKz4RO', true, false,
        'ALL', 1625127990000, 1627200965049);