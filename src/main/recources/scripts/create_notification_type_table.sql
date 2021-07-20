DROP TABLE IF EXISTS notification_type;
DROP TYPE IF EXISTS notification_type_code;
DROP TYPE IF EXISTS notification_type_name;

CREATE TYPE notification_type_code as ENUM ('PST', 'PTC', 'CMC', 'FRR', 'MSG');
CREATE TYPE notification_type_name as ENUM ('POST', 'POST_COMMENT', 'COMMENT_COMMENT', 'FRIEND_REQUEST', 'MESSAGE');

CREATE TABLE notification_type(
id SERIAL NOT NULL,
code notification_type_code,
name notification_type_name,
PRIMARY KEY(id)
);