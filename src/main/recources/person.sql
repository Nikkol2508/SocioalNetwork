CREATE TYPE permission_type as ENUM ('ALL', 'FRIENDS');

CREATE TABLE person 
(
	Id SERIAL PRIMARY KEY  NOT NULL,
	first_name CHARACTER VARYING(255)  NOT NULL,
	last_name CHARACTER VARYING(255)  NOT NULL,
	reg_date  TIMESTAMP NOT NULL,
	birth_date DATE,
	e_mail CHARACTER VARYING(255)  NOT NULL,
	phone CHARACTER VARYING(255) NOT NULL,
	password CHARACTER VARYING(255)  NOT NULL,
	photo TEXT,
	about TEXT, 
	town CHARACTER VARYING(255),
	confirmation_code CHARACTER VARYING(255),
	is_approved BOOLEAN,
	messages_permission permission_type,
	last_online_time TIMESTAMP 
);