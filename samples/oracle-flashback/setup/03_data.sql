ALTER SESSION SET CONTAINER = localpdb;
ALTER SESSION SET CURRENT_SCHEMA = scott;

CREATE TABLE brand (
    id NUMBER PRIMARY KEY,
    name VARCHAR2(45)
);
CREATE TABLE guitar (
    id NUMBER GENERATED AS IDENTITY PRIMARY KEY,
    brand_id NUMBER,
    model VARCHAR2(45),
    CONSTRAINT guitar_brand_fk
        FOREIGN KEY (brand_id) REFERENCES brand (id)
);

INSERT INTO brand VALUES (1, 'Fender');
INSERT INTO brand VALUES (2, 'Gibson');
INSERT INTO guitar (brand_id, model) VALUES (1, 'Stratocaster');
INSERT INTO guitar (brand_id, model) VALUES (1, 'Mustang');
INSERT INTO guitar (brand_id, model) VALUES (2, 'Firebird');
INSERT INTO guitar (brand_id, model) VALUES (2, 'ES-335');

CREATE RESTORE POINT before_feature;
