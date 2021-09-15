--liquibase formatted sql
--changeset andrei:alter-notification-column

ALTER TABLE notification
    ADD COLUMN type TEXT NOT NULL;
ALTER TABLE notification
    ADD COLUMN name TEXT NOT NULL;
ALTER TABLE notification
    DROP type_id;
DROP TABLE IF EXISTS notification_type;
