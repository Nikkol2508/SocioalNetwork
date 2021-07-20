CREATE TYPE message_status_type as ENUM ('SENT', 'READ');

CREATE TABLE IF NOT EXISTS messages
(
	id SERIAL NOT NULL,
	time TIMESTAMP NOT NULL,
	author_id INT NOT NULL,
	recipient_id INT NOT NULL,
 	message_text TEXT,
	read_status message_status_type,
	PRIMARY KEY(id)
);
