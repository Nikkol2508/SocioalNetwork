--liquibase formatted sql
--changeset aleksandr:create-indexes-for-all-tables

CREATE INDEX ON post_comment (parent_id);

CREATE INDEX ON dialog (first_user_id);

CREATE INDEX ON dialog (second_user_id);

CREATE INDEX ON dialog (first_user_id, second_user_id);

CREATE INDEX ON image (name);

CREATE INDEX ON post_like (item_id);

CREATE INDEX ON post_like (item_id, type);

CREATE INDEX ON post_like (item_id, type, person_id);

CREATE INDEX ON message (id, dialog_id);

CREATE INDEX ON message (recipient_id, read_status);

CREATE INDEX ON message (recipient_id, author_id, time);

CREATE INDEX ON message (recipient_id, dialog_id, read_status);

CREATE INDEX ON notification (person_id);

CREATE INDEX ON notification (person_id, name);

CREATE INDEX ON notification_settings (type_id);

CREATE INDEX ON notification_settings (person_id);

CREATE INDEX ON notification_setting_type (id, code);

CREATE INDEX ON person (reg_date);

CREATE INDEX ON person (first_name);

CREATE INDEX ON friendship (src_person_id);

CREATE INDEX ON friendship (dst_person_id);

CREATE INDEX ON friendship (src_person_id, dst_person_id);

CREATE INDEX ON friendship (status_id);

CREATE INDEX ON friendship_status(id, code);

CREATE INDEX ON post (time, is_blocked);

CREATE INDEX ON post (author_id, time, is_blocked);

CREATE INDEX ON post (post_text, is_blocked);

CREATE INDEX ON tag (tag);

CREATE INDEX ON post2tag (tag_id);

CREATE INDEX ON post2tag (tag_id, post_id);
