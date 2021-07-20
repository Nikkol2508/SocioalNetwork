CREATE TYPE user_type as ENUM ('MODERATOR', 'ADMIN');

CREATE TABLE IF NOT EXISTS users
(
	id SERIAL NOT NULL,
	name TEXT  NOT NULL,
	e_mail TEXT  NOT NULL,
	password TEXT  NOT NULL,
	type user_type NOT NULL,
  PRIMARY KEY(id)
);