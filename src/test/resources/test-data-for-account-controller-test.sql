INSERT INTO person (first_name, last_name, reg_date, e_mail, password, messages_permission)
VALUES ('First', 'Last', 1625127990000, 'test@test.ru', '$2a$12$RvaD2mYu8Mv7EUD66PiGNeY8gtbgX9L2IVRs.AUzx1w8o3DGKz4RO',
        'ALL');

UPDATE person
SET confirmation_code = 'uniqueTokenForVasy'
WHERE e_mail = 'vasy@yandex.ru';

UPDATE person
SET confirmation_code = 'uniqueTokenForHoma'
WHERE e_mail = 'homa@yandex.ru';