--liquibase formatted sql
--changeset andrei:alter-notification-column

ALTER TABLE notification ADD COLUMN src_person_id INT NOT NULL;
ALTER TABLE notification ADD FOREIGN KEY (src_person_id) REFERENCES person(id) ON DELETE RESTRICT;