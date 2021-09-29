INSERT INTO post_comment (time, post_id, parent_id, author_id, comment_text, is_blocked)
VALUES (1629464930000, 1, null, 1, 'test_comment', false),
       (1629464930000, 14, 10, 8, 'test_comment2', false),
       (1629464930000, 13, 8, 4, 'test_comment2', false);

INSERT INTO post (time, author_id, title, post_text, is_blocked)
VALUES (1629464930000, 1, 'test_post', 'test_text', false);

