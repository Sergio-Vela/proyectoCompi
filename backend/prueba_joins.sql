CREATE DATABASE empresa;
USE empresa;

CREATE TABLE empleados (id INT PRIMARY KEY, nombre VARCHAR(50), departamento_id INT);
INSERT INTO empleados VALUES (1, 'Juan', 10);
INSERT INTO empleados VALUES (2, 'Maria', 20);
INSERT INTO empleados VALUES (3, 'Pedro', 10);
INSERT INTO empleados VALUES (4, 'Ana', 30);

CREATE TABLE departamentos (id INT PRIMARY KEY, nombre VARCHAR(50), ciudad VARCHAR(50));
INSERT INTO departamentos VALUES (10, 'Ventas', 'Madrid');
INSERT INTO departamentos VALUES (20, 'IT', 'Barcelona');
INSERT INTO departamentos VALUES (30, 'RH', 'Valencia');

SELECT * FROM empleados;

SELECT * FROM departamentos;

SELECT empleados.nombre, departamentos.nombre FROM empleados INNER JOIN departamentos ON empleados.departamento_id = departamentos.id;

SELECT empleados.nombre, departamentos.ciudad FROM empleados INNER JOIN departamentos ON empleados.departamento_id = departamentos.id WHERE empleados.departamento_id = 10;
