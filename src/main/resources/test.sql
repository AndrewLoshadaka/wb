DROP TABLE IF EXISTS reviews;

CREATE TABLE reviews (
  id INT PRIMARY KEY,
  author VARCHAR(250) NOT NULL,
  status VARCHAR(250) NOT NULL
);

INSERT INTO reviews (ID, author, status) VALUES
  (1, 'first', 'last 1'),
  (2, 'first', 'last 2');