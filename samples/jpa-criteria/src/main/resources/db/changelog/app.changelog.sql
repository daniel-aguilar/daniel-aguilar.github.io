--liquibase formatted sql
--changeset Daniel Aguilar:app
CREATE TABLE brand (
	id INT PRIMARY KEY,
	name VARCHAR
);

CREATE TABLE model (
	id INT PRIMARY KEY,
	brand_id INT,
	name VARCHAR,
	string_count TINYINT DEFAULT 6,
	is_hollow_body BOOLEAN DEFAULT FALSE,

	CONSTRAINT model_brand_fk
		FOREIGN KEY (brand_id) REFERENCES brand (id)
);

CREATE TABLE body_finish (
	id INT PRIMARY KEY,
	color VARCHAR
);

CREATE TABLE fretboard_wood (
	id INT PRIMARY KEY,
	name VARCHAR
);

CREATE TABLE store (
	id INT PRIMARY KEY,
	location VARCHAR
);

CREATE TABLE guitar (
	serial_number INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	store_id INT,
	model_id INT,
	body_finish_id INT,
	fretboard_wood_id INT,
	price NUMERIC,

	CONSTRAINT guitar_store_fk
		FOREIGN KEY (store_id) REFERENCES store (id),
	CONSTRAINT guitar_model_fk
		FOREIGN KEY (model_id) REFERENCES model (id),
	CONSTRAINT guitar_body_finish_fk
		FOREIGN KEY (body_finish_id) REFERENCES body_finish (id),
	CONSTRAINT guitar_fretboard_wood_fk
		FOREIGN KEY (fretboard_wood_id) REFERENCES fretboard_wood (id)
);


INSERT INTO store (id, location) VALUES (1, 'Roseville');
INSERT INTO store (id, location) VALUES (2, 'Folsom');

INSERT INTO brand (id, name) VALUES (1, 'Fender');
INSERT INTO brand (id, name) VALUES (2, 'Gibson');
INSERT INTO brand (id, name) VALUES (3, 'Ibanez');
INSERT INTO brand (id, name) VALUES (4, 'Music Man');

INSERT INTO body_finish (id, color) VALUES (1, 'Sunburst');
INSERT INTO body_finish (id, color) VALUES (2, 'Cherry');
INSERT INTO body_finish (id, color) VALUES (3, 'Ebony');
INSERT INTO body_finish (id, color) VALUES (4, 'Blue');

INSERT INTO fretboard_wood (id, name) VALUES (1, 'Rosewood');
INSERT INTO fretboard_wood (id, name) VALUES (2, 'Maple');
INSERT INTO fretboard_wood (id, name) VALUES (3, 'Ebony');

INSERT INTO model (id, brand_id, name) VALUES (1, 1, 'Stratocaster');
INSERT INTO model (id, brand_id, name) VALUES (2, 1, 'Telecaster');
INSERT INTO model (id, brand_id, name) VALUES (3, 2, 'Les Paul');
INSERT INTO model (id, brand_id, name, is_hollow_body) VALUES (4, 2, 'ES', TRUE);
INSERT INTO model (id, brand_id, name, string_count) VALUES (5, 3, 'X', 7);
INSERT INTO model (id, brand_id, name, string_count) VALUES (6, 3, 'RGD', 8);
INSERT INTO model (id, brand_id, name) VALUES (7, 4, 'StingRay');
INSERT INTO model (id, brand_id, name, string_count) VALUES (8, 4, 'Majesty', 7);

INSERT INTO guitar (store_id, model_id,
	body_finish_id, fretboard_wood_id, price)
VALUES
	(1, 1, 1, 2, 150),
	(1, 1, 4, 1, 150),
	(1, 2, 1, 1, 100),
	(1, 3, 3, 1, 200),
	(1, 4, 2, 1, 250),
	(1, 7, 1, 1, 300),

	(2, 5, 3, 1, 100),
	(2, 5, 2, 2, 125),
	(2, 6, 4, 1, 150),
	(2, 8, 3, 1, 300),
	(2, 8, 2, 2, 300),
	(2, 4, 1, 2, 250);
