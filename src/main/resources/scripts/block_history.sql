CREATE TYPE action_type as ENUM ('BLOCK', 'UNBLOCK');

CREATE TABLE IF NOT EXISTS block_history
(
	id SERIAL  NOT NULL,
	time TIMESTAMP NOT NULL,
	person_id INT NOT NULL,
	post_id INT NOT NULL,
	comment_id INT NOT NULL,
	action action_type ,
  PRIMARY KEY(id),
  FOREIGN KEY(person_id) REFERENCES person(id) ON DELETE RESTRICT,
  FOREIGN KEY(post_id) REFERENCES post(id) ON DELETE RESTRICT,
  FOREIGN KEY(comment_id) REFERENCES post_comment(id) ON DELETE RESTRICT
);