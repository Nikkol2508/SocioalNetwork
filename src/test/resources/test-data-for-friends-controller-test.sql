
INSERT INTO friendship_status (time, name, code)
VALUES (1629464930000, 'test', 'REQUEST');

INSERT INTO friendship (status_id, src_person_id, dst_person_id)
VALUES ((SELECT id FROM friendship_status WHERE name = 'test'), 3, 1);

INSERT INTO friendship_status (time, name, code)
VALUES (1629464930000, 'test1', 'FRIEND');

INSERT INTO friendship (status_id, src_person_id, dst_person_id)
VALUES ((SELECT id FROM friendship_status WHERE name = 'test1'), 3, 4);

INSERT INTO friendship_status (time, name, code)
VALUES (1629464930000, 'test2', 'FRIEND');

INSERT INTO friendship (status_id, src_person_id, dst_person_id)
VALUES ((SELECT id FROM friendship_status WHERE name = 'test1'), 1, 10);