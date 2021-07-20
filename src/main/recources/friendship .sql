CREATE TABLE friendship 
(
	id SERIAL PRIMARY KEY  NOT NULL,
	status_id INT NOT NULL,
	src_person_id INT NOT NULL,
	dst_person_id INT NOT NULL
);