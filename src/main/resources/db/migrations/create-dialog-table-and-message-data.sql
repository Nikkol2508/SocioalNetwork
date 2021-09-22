--liquibase formatted sql
--changeset vitaly:create-dialog-table-and-message-data

CREATE TABLE IF NOT EXISTS dialog
(
    id             SERIAL NOT NULL,
    first_user_id  INT    NOT NULL,
    second_user_id INT    NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (first_user_id) REFERENCES person (id) ON DELETE CASCADE,
    FOREIGN KEY (second_user_id) REFERENCES person (id) ON DELETE CASCADE
);

INSERT INTO dialog(first_user_id, second_user_id)
VALUES (1, 2);

ALTER TABLE message
    ADD COLUMN dialog_id INT NOT NULL;

ALTER TABLE message
    ADD FOREIGN KEY (dialog_id) REFERENCES dialog (id) ON DELETE CASCADE;

INSERT INTO message(time, author_id, recipient_id, message_text, read_status, dialog_id)
VALUES (1629463864000, 1, 2, 'Привет :)', 'READ', 1),
       (1629463924000, 2, 1, 'Привет!', 'READ', 1),
       (1629463930000, 1, 2, 'Как дела?', 'SENT', 1)