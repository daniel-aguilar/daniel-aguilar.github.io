INSERT INTO guitar (brand_id, model) VALUES (2, 'Les Paul');
CREATE TABLE guitar_catalog (
    id NUMBER GENERATED AS IDENTITY,
    guitar_id NUMBER,
    quantity NUMBER,
    CONSTRAINT guitar_catalog_fk
        FOREIGN KEY (guitar_id) REFERENCES guitar (id)
);
