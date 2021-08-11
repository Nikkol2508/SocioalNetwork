--liquibase formatted sql
--changeset vitaly:person-email-unique

ALTER TABLE person
    ADD CONSTRAINT idx_email_unique UNIQUE (e_mail);