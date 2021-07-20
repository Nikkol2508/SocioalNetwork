CREATE TYPE action_type as ENUM ('BLOCK', 'UNBLOCK');

CREATE TABLE block_history 
(
	id SERIAL PRIMARY KEY  NOT NULL,
	time TIMESTAMP NOT NULL,
	person_id INT NOT NULL,
	post_id INT NOT NULL,
	comment_id INT NOT NULL,
	action action_type 

);