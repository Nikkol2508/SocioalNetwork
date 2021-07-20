DROP TABLE IF EXISTS post;

CREATE TABLE post
(
    id         SERIAL       NOT NULL,
    time       TIMESTAMP    NOT NULL,
    author_id  INT          NOT NULL,
    tittle     VARCHAR(100) NOT NULL,
    post_text  TEXT         NOT NULL,
    is_blocked BOOLEAN      NOT NULL,
    PRIMARY KEY (id)
);