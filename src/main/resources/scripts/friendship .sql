CREATE TABLE IF NOT EXISTS friendship
(
	id SERIAL NOT NULL,
	status_id INT NOT NULL,
	src_person_id INT NOT NULL,
	dst_person_id INT NOT NULL,
	PRIMARY KEY(id),
	FOREIGN KEY(status_id) REFERENCES friendship_status(id) ON DELETE RESTRICT,
	FOREIGN KEY(src_person_id) REFERENCES person(id) ON DELETE RESTRICT,
	FOREIGN KEY(dst_person_id) REFERENCES person(id) ON DELETE RESTRICT
);