CREATE TABLE IF NOT EXISTS friendship
(
	id SERIAL NOT NULL,
	status_id INT NOT NULL,
	src_person_id INT NOT NULL,
	dst_person_id INT NOT NULL,
	PRIMARY KEY(id)
);