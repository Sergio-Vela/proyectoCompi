CREATE DATABASE prueba_drop_delete;
USE prueba_drop_delete;

CREATE TABLE usuarios (id INT, nombre VARCHAR(50), PRIMARY KEY (id));
INSERT INTO usuarios VALUES (1, 'Juan');
INSERT INTO usuarios VALUES (2, 'Maria');
INSERT INTO usuarios VALUES (3, 'Pedro');

SELECT * FROM usuarios;

DELETE FROM usuarios WHERE id = 2;

SELECT * FROM usuarios;

CREATE TABLE productos (id_prod INT, nombre_prod VARCHAR(100), PRIMARY KEY (id_prod));
INSERT INTO productos VALUES (100, 'Laptop');
INSERT INTO productos VALUES (101, 'Mouse');

SELECT * FROM productos;

DROP TABLE productos;   
DROP DATABASE prueba_drop_delete;
