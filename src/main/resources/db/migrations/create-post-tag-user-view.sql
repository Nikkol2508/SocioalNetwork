--liquibase formatted sql
--changeset vitaly:create-post-tag-user-view

CREATE VIEW post_tag_user_view AS
SELECT post.id,
       post."time",
       post.author_id,
       post.title,
       post.post_text,
       post.is_blocked,
       tag.tag,
       person.first_name,
       person.last_name
FROM tag,
     post,
     post2tag,
     person
WHERE post.id = post2tag.post_id
  AND tag.id = post2tag.tag_id
  AND post.is_blocked = false
  AND post.author_id = person.id;