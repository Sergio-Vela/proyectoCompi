CREATE DATABASE tienda;
USE tienda;

CREATE TABLE productos (id INT PRIMARY KEY, nombre VARCHAR(50), categoria VARCHAR(30), precio DECIMAL(10,2));
INSERT INTO productos VALUES (1, 'Laptop', 'Electrónica', 1500.00);
INSERT INTO productos VALUES (2, 'Mouse', 'Accesorios', 25.00);
INSERT INTO productos VALUES (3, 'Teclado', 'Accesorios', 75.00);
INSERT INTO productos VALUES (4, 'Monitor', 'Electrónica', 300.00);
INSERT INTO productos VALUES (5, 'Cable USB', 'Accesorios', 10.00);

SELECT * FROM productos;

SELECT categoria, SUM(precio) FROM productos GROUP BY categoria;

SELECT categoria, AVG(precio) FROM productos GROUP BY categoria;

SELECT categoria, COUNT(*) FROM productos GROUP BY categoria;
