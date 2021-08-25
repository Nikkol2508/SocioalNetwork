--liquibase formatted sql
--changeset nikolai:like-corrections

ALTER TABLE post_like DROP CONSTRAINT post_like_post_id_fkey;

ALTER TABLE post_like RENAME COLUMN post_id TO item_id;

ALTER TABLE post_like ADD type TEXT NOT NULL DEFAULT 'Post';