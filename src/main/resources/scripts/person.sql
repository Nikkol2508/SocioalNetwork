CREATE TYPE permission_type as ENUM ('ALL', 'FRIENDS');

CREATE TABLE IF NOT EXISTS person
(
	Id SERIAL  NOT NULL,
	first_name TEXT NOT NULL,
	last_name TEXT NOT NULL,
	reg_date  TIMESTAMP NOT NULL,
	birth_date DATE,
	e_mail TEXT  NOT NULL,
	phone TEXT NOT NULL,
	password TEXT NOT NULL,
	photo TEXT,
	about TEXT, 
	town TEXT,
	confirmation_code TEXT,
	is_approved BOOLEAN,
	messages_permission permission_type,
	last_online_time TIMESTAMP,
	PRIMARY KEY(id)
);