INSERT INTO dialog (first_user_id, second_user_id)
VALUES (2, 3),
       (4, 5),
       (6, 7),
       (8, 9);

INSERT INTO message (time, author_id, recipient_id, message_text, read_status, dialog_id)
VALUES (1629464930000, 2, 3, 'Test', 'SENT', 2),
       (1629464930000, 4, 5, 'Test1', 'SENT', 3),
       (1629464930000, 6, 7, 'Test2', 'SENT', 4),
       (1629464930000, 7, 6, 'Test3', 'SENT', 4),
       (1629464930000, 8, 9, 'Test4', 'SENT', 5);