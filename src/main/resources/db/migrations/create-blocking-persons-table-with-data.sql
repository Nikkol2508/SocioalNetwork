CREATE TABLE IF NOT EXISTS blocking_persons
(
    blocking_person_id INT NOT NULL,
    blocked_person_id  INT NOT NULL,
    PRIMARY KEY (blocking_person_id, blocked_person_id),
    FOREIGN KEY (blocking_person_id) REFERENCES person (id) ON DELETE CASCADE,
    FOREIGN KEY (blocked_person_id) REFERENCES person (id) ON DELETE CASCADE
);

INSERT INTO blocking_persons (blocking_person_id, blocked_person_id)
VALUES (2, 4);