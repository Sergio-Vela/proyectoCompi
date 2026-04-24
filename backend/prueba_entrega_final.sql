CREATE DATABASE banco;
USE banco;
 
CREATE TABLE tb_analisis (
  idcuenta INT,
  idproducto INT
);

CREATE TABLE tbBitacora (
  idbitacora INT,
  Fecha VARCHAR(50),
  idmotivoconsulta INT,
  idCliente INT,
  Consulta VARCHAR(4000)
);
  
CREATE TABLE tbCliente (
  idCLiente INT,
  PrimerNombre VARCHAR(50),
  SegundoNombre VARCHAR(50),
  TercerNombre VARCHAR(50),
  PrimerApellido VARCHAR(50),
  SegundoApellido VARCHAR(50),
  ApellidodeCasada VARCHAR(50),
  Fechanacimiento VARCHAR(50),
  Genero VARCHAR(10),
  EstadoCivil VARCHAR(20),
  ProfesionuOficio VARCHAR(50),
  EstaLaborando VARCHAR(20),
  idSucursal INT,
  FechaCreacion VARCHAR(50),
  DireccionLaboral VARCHAR(200),
  TelefonoLaboral VARCHAR(50)
);
 
CREATE TABLE tbDireccion (
  idCLiente INT,
  Direccion VARCHAR(200),
  idEstado INT,
  idDireccion INT,
  idUbigeo1 INT,
  idUbigeo2 INT
);
 
CREATE TABLE tbEstado (
  idEstado INT,
  Estado VARCHAR(50)
);
 
CREATE TABLE tbhistorialcrediticio (
  idCuenta INT,
  FechaCorte VARCHAR(50),
  SaldoActual DECIMAL(10,2),
  SaldoMora DECIMAL(10,2),
  diasmora INT,
  idEstado VARCHAR(10),
  alturamora INT
);
 
CREATE TABLE tbIdentificacionCliente (
  idCliente INT,
  Documento VARCHAR(50),
  idtipodocumento INT
);
 
CREATE TABLE tbObligacion (
  idCuenta INT,
  idProducto INT,
  idcliente INT,
  FechaAperturo VARCHAR(50),
  FechaVencimiento VARCHAR(50),
  Limite DECIMAL(18,4),
  FechaUltimoCorte VARCHAR(50)
);
 
CREATE TABLE tbSursal (
  idSucursal INT,
  NombreSucursal VARCHAR(100),
  Direccion VARCHAR(200),
  Telefono VARCHAR(50)
);
 
CREATE TABLE tbtipodocumento (
  idtipodocumento INT,
  tipodocumento VARCHAR(50),
  idEstado INT
);
 
CREATE TABLE tbtipoOperacion (
  idtipoOperacion INT,
  nombreOperacion VARCHAR(100)
);
 
CREATE TABLE tbtipoProducto (
  idProducto INT,
  NombreProducto VARCHAR(100),
  idtipoOperacion INT,
  idEstado INT,
  tasainteres VARCHAR(20),
  montominimo VARCHAR(20),
  montomaximo DECIMAL(18,2),
  idtipoGarantia INT,
  plazominimo INT,
  plazomaximo INT,
  fechacreacion VARCHAR(50)
);
 
CREATE TABLE tbUbicacionGeografica2 (
  idUbigeo2 INT,
  idUbigeo1 INT,
  Nombre VARCHAR(50)
);
 
CREATE TABLE tbUbigacionGeografica (
  idUbigeo1 INT,
  NombreUbigeo VARCHAR(50)
);



INSERT INTO tb_analisis (idcuenta, idproducto) VALUES (1, 1);
INSERT INTO tb_analisis (idcuenta, idproducto) VALUES (3, 1);
 
INSERT INTO tbBitacora (idbitacora, Fecha, idmotivoconsulta, idCliente, Consulta) VALUES (2, "2024/04/01", 1, 1, "Julio Alfredo");
INSERT INTO tbBitacora (idbitacora, Fecha, idmotivoconsulta, idCliente, Consulta) VALUES (5, "2024/04/02", 1, 2, "Luisa Fernanda");
INSERT INTO tbBitacora (idbitacora, Fecha, idmotivoconsulta, idCliente, Consulta) VALUES (6, "2024/04/03", 1, 3, "Pedro Guillermo");
INSERT INTO tbBitacora (idbitacora, Fecha, idmotivoconsulta, idCliente, Consulta) VALUES (8, "2024/04/04", 1, 4, "Juan Ramon");
 
INSERT INTO tbCliente (idCLiente, PrimerNombre, SegundoNombre, TercerNombre, PrimerApellido, SegundoApellido, ApellidodeCasada, Fechanacimiento, Genero, EstadoCivil, ProfesionuOficio, EstaLaborando, idSucursal, FechaCreacion, DireccionLaboral, TelefonoLaboral) VALUES (1, "Julio", "Alfredo", "Tercero", "Rodriguez", "Juarez", "", "1990/01/15", "M", "casado", "Contador", "Activo", 1, "2024/04/01", "", "");
INSERT INTO tbCliente (idCLiente, PrimerNombre, SegundoNombre, TercerNombre, PrimerApellido, SegundoApellido, ApellidodeCasada, Fechanacimiento, Genero, EstadoCivil, ProfesionuOficio, EstaLaborando, idSucursal, FechaCreacion, DireccionLaboral, TelefonoLaboral) VALUES (2, "Luisa", "Fernanda", "Maria", "Leon", "Lopez", "de Perez", "1985/07/22", "F", "casada", "Secretaria", "Activo", 1, "2024/04/02", "", "");
INSERT INTO tbCliente (idCLiente, PrimerNombre, SegundoNombre, TercerNombre, PrimerApellido, SegundoApellido, ApellidodeCasada, Fechanacimiento, Genero, EstadoCivil, ProfesionuOficio, EstaLaborando, idSucursal, FechaCreacion, DireccionLaboral, TelefonoLaboral) VALUES (3, "Pedro", "Guillermo", "", "Salazar", "Hernandez", "", "1982/11/10", "M", "casado", "Contador", "Activo", 1, "2024/04/03", "", "");
INSERT INTO tbCliente (idCLiente, PrimerNombre, SegundoNombre, TercerNombre, PrimerApellido, SegundoApellido, ApellidodeCasada, Fechanacimiento, Genero, EstadoCivil, ProfesionuOficio, EstaLaborando, idSucursal, FechaCreacion, DireccionLaboral, TelefonoLaboral) VALUES (4, "Pedro", "Guillermo", "", "Salazar", "Hernandez", "", "1982/11/10", "M", "casado", "Contador", "Activo", 1, "2024/04/04", "", "");
INSERT INTO tbCliente (idCLiente, PrimerNombre, SegundoNombre, TercerNombre, PrimerApellido, SegundoApellido, ApellidodeCasada, Fechanacimiento, Genero, EstadoCivil, ProfesionuOficio, EstaLaborando, idSucursal, FechaCreacion, DireccionLaboral, TelefonoLaboral) VALUES (5, "Pedro", "Guillermo", "", "Salazar", "Hernandez", "", "1982/11/10", "M", "casado", "Contador", "Activo", 1, "2024/04/05", "", "");
 
INSERT INTO tbDireccion (idCLiente, Direccion, idEstado, idDireccion, idUbigeo1, idUbigeo2) VALUES (2, "Ciudad", 1, 1, 1, 1);
 
INSERT INTO tbEstado (idEstado, Estado) VALUES (1, "Activo");
INSERT INTO tbEstado (idEstado, Estado) VALUES (2, "Inactivo");
INSERT INTO tbEstado (idEstado, Estado) VALUES (3, "Vigente");
INSERT INTO tbEstado (idEstado, Estado) VALUES (4, "Cancelado");
INSERT INTO tbEstado (idEstado, Estado) VALUES (5, "Mora");
INSERT INTO tbEstado (idEstado, Estado) VALUES (6, "Otros");
INSERT INTO tbEstado (idEstado, Estado) VALUES (7, "Nuevo");
INSERT INTO tbEstado (idEstado, Estado) VALUES (8, "Nuevo");
INSERT INTO tbEstado (idEstado, Estado) VALUES (10, "Nuevo");
INSERT INTO tbEstado (idEstado, Estado) VALUES (12, "Nuevo");
 
INSERT INTO tbhistorialcrediticio (idCuenta, FechaCorte, SaldoActual, SaldoMora, diasmora, idEstado, alturamora) VALUES (1, "2024/01/01", 0.00, 0.00, 0, "3", 0);
INSERT INTO tbhistorialcrediticio (idCuenta, FechaCorte, SaldoActual, SaldoMora, diasmora, idEstado, alturamora) VALUES (1, "2024/02/01", 100.00, 0.00, 0, "3", 0);
INSERT INTO tbhistorialcrediticio (idCuenta, FechaCorte, SaldoActual, SaldoMora, diasmora, idEstado, alturamora) VALUES (1, "2024/03/01", 500.00, 0.00, 0, "3", 0);
INSERT INTO tbhistorialcrediticio (idCuenta, FechaCorte, SaldoActual, SaldoMora, diasmora, idEstado, alturamora) VALUES (1, "2024/04/01", 800.00, 0.00, 0, "3", 0);
INSERT INTO tbhistorialcrediticio (idCuenta, FechaCorte, SaldoActual, SaldoMora, diasmora, idEstado, alturamora) VALUES (1, "2024/05/01", 0.00, 0.00, 0, "3", 0);
INSERT INTO tbhistorialcrediticio (idCuenta, FechaCorte, SaldoActual, SaldoMora, diasmora, idEstado, alturamora) VALUES (1, "2024/06/01", 1000.00, 0.00, 0, "3", 0);
INSERT INTO tbhistorialcrediticio (idCuenta, FechaCorte, SaldoActual, SaldoMora, diasmora, idEstado, alturamora) VALUES (1, "2024/07/01", 1100.00, 0.00, 0, "3", 0);
INSERT INTO tbhistorialcrediticio (idCuenta, FechaCorte, SaldoActual, SaldoMora, diasmora, idEstado, alturamora) VALUES (1, "2024/08/01", 1100.00, 1100.00, 32, "3", 0);
INSERT INTO tbhistorialcrediticio (idCuenta, FechaCorte, SaldoActual, SaldoMora, diasmora, idEstado, alturamora) VALUES (1, "2024/09/01", 1100.00, 1100.00, 60, "3", 0);
INSERT INTO tbhistorialcrediticio (idCuenta, FechaCorte, SaldoActual, SaldoMora, diasmora, idEstado, alturamora) VALUES (1, "2024/10/01", 1100.00, 1100.00, 60, "3", 0);
 
INSERT INTO tbhistorialcrediticio (idCuenta, FechaCorte, SaldoActual, SaldoMora, diasmora, idEstado, alturamora) VALUES (2, "2024/01/01", 0.00, 0.00, 0, "3", 0);
INSERT INTO tbhistorialcrediticio (idCuenta, FechaCorte, SaldoActual, SaldoMora, diasmora, idEstado, alturamora) VALUES (2, "2024/02/01", 105.00, 0.00, 0, "3", 0);
INSERT INTO tbhistorialcrediticio (idCuenta, FechaCorte, SaldoActual, SaldoMora, diasmora, idEstado, alturamora) VALUES (2, "2024/03/01", 525.00, 0.00, 0, "3", 0);
INSERT INTO tbhistorialcrediticio (idCuenta, FechaCorte, SaldoActual, SaldoMora, diasmora, idEstado, alturamora) VALUES (2, "2024/04/01", 840.00, 0.00, 0, "3", 0);
 
INSERT INTO tbIdentificacionCliente (idCliente, Documento, idtipodocumento) VALUES (1, "4578545545", 1);
INSERT INTO tbIdentificacionCliente (idCliente, Documento, idtipodocumento) VALUES (1, "78878-5", 2);
INSERT INTO tbIdentificacionCliente (idCliente, Documento, idtipodocumento) VALUES (2, "988545515", 1);
INSERT INTO tbIdentificacionCliente (idCliente, Documento, idtipodocumento) VALUES (3, "55485545", 1);
 
INSERT INTO tbObligacion (idCuenta, idProducto, idcliente, FechaAperturo, FechaVencimiento, Limite, FechaUltimoCorte) VALUES (1, 1, 1, "2024/01/01", "2024/12/31", 200.0000, "2024/06/01");
INSERT INTO tbObligacion (idCuenta, idProducto, idcliente, FechaAperturo, FechaVencimiento, Limite, FechaUltimoCorte) VALUES (2, 4, 1, "2024/02/01", "2024/12/31", 25000.0000, "2024/06/01");
INSERT INTO tbObligacion (idCuenta, idProducto, idcliente, FechaAperturo, FechaVencimiento, Limite, FechaUltimoCorte) VALUES (3, 1, 3, "2024/03/01", "2024/12/31", 800.0000, "2024/06/01");
INSERT INTO tbObligacion (idCuenta, idProducto, idcliente, FechaAperturo, FechaVencimiento, Limite, FechaUltimoCorte) VALUES (4, 3, 2, "2024/04/01", "2024/12/31", 11800.0000, "2024/06/01");
 
INSERT INTO tbSursal (idSucursal, NombreSucursal, Direccion, Telefono) VALUES (1, "Central", "Ciudad", "122122");
INSERT INTO tbSursal (idSucursal, NombreSucursal, Direccion, Telefono) VALUES (2, "Agencia zona 1", "Ciudad", "122122");
 
INSERT INTO tbtipodocumento (idtipodocumento, tipodocumento, idEstado) VALUES (1, "DPI", 1);
INSERT INTO tbtipodocumento (idtipodocumento, tipodocumento, idEstado) VALUES (2, "NIT", 1);
 
INSERT INTO tbtipoOperacion (idtipoOperacion, nombreOperacion) VALUES (1, "Captaciones");
INSERT INTO tbtipoOperacion (idtipoOperacion, nombreOperacion) VALUES (2, "Colocaciones");
 
INSERT INTO tbtipoProducto (idProducto, NombreProducto, idtipoOperacion, idEstado, tasainteres, montominimo, montomaximo, idtipoGarantia, plazominimo, plazomaximo, fechacreacion) VALUES (1, "Tarjeta de Credito Basica", 2, 1, "25.5", "100", 5000.00, 1, 0, 0, "2024/04/01");
INSERT INTO tbtipoProducto (idProducto, NombreProducto, idtipoOperacion, idEstado, tasainteres, montominimo, montomaximo, idtipoGarantia, plazominimo, plazomaximo, fechacreacion) VALUES (2, "Tarjeta de Credito Empresas", 2, 1, "17.5", "10000", 75000.00, 1, 0, 0, "2024/04/01");
INSERT INTO tbtipoProducto (idProducto, NombreProducto, idtipoOperacion, idEstado, tasainteres, montominimo, montomaximo, idtipoGarantia, plazominimo, plazomaximo, fechacreacion) VALUES (3, "Tarjeta de Credito Internacional", 2, 1, "20.5", "800", 10000.00, 1, 0, 0, "2024/04/01");
INSERT INTO tbtipoProducto (idProducto, NombreProducto, idtipoOperacion, idEstado, tasainteres, montominimo, montomaximo, idtipoGarantia, plazominimo, plazomaximo, fechacreacion) VALUES (4, "Prestamo Personal", 2, 1, "35.5", "10000", 100000.00, 1, 0, 0, "2024/04/01");
INSERT INTO tbtipoProducto (idProducto, NombreProducto, idtipoOperacion, idEstado, tasainteres, montominimo, montomaximo, idtipoGarantia, plazominimo, plazomaximo, fechacreacion) VALUES (5, "Prestamo Hipotecario", 2, 1, "9.01", "150000", 500000.00, 1, 0, 0, "2024/04/01");
INSERT INTO tbtipoProducto (idProducto, NombreProducto, idtipoOperacion, idEstado, tasainteres, montominimo, montomaximo, idtipoGarantia, plazominimo, plazomaximo, fechacreacion) VALUES (6, "Prestamo Prendario", 2, 1, "45.5", "100", 5000.00, 1, 0, 0, "2024/04/01");
 
INSERT INTO tbUbicacionGeografica2 (idUbigeo2, idUbigeo1, Nombre) VALUES (1, 1, "Guatemala");
INSERT INTO tbUbicacionGeografica2 (idUbigeo2, idUbigeo1, Nombre) VALUES (2, 1, "Villa Nueva");
INSERT INTO tbUbicacionGeografica2 (idUbigeo2, idUbigeo1, Nombre) VALUES (3, 1, "Mixco");
INSERT INTO tbUbicacionGeografica2 (idUbigeo2, idUbigeo1, Nombre) VALUES (4, 1, "San Jose Pinula");
INSERT INTO tbUbicacionGeografica2 (idUbigeo2, idUbigeo1, Nombre) VALUES (5, 2, "Coban");
 
INSERT INTO tbUbigacionGeografica (idUbigeo1, NombreUbigeo) VALUES (1, "Guatemala");
INSERT INTO tbUbigacionGeografica (idUbigeo1, NombreUbigeo) VALUES (2, "Alta Verapaz");
INSERT INTO tbUbigacionGeografica (idUbigeo1, NombreUbigeo) VALUES (3, "Baja Verapaz");
INSERT INTO tbUbigacionGeografica (idUbigeo1, NombreUbigeo) VALUES (4, "Chimaltenango");
INSERT INTO tbUbigacionGeografica (idUbigeo1, NombreUbigeo) VALUES (5, "Quetzaltenango");
INSERT INTO tbUbigacionGeografica (idUbigeo1, NombreUbigeo) VALUES (6, "Totonicapan");
 
USE banco;
SELECT * FROM tb_analisis;
SELECT * FROM tbBitacora;
SELECT * FROM tbCliente;
SELECT * FROM tbDireccion;
SELECT * FROM tbEstado;
SELECT * FROM tbhistorialcrediticio;
SELECT * FROM tbIdentificacionCliente;
SELECT * FROM tbObligacion;
SELECT * FROM tbSursal;
SELECT * FROM tbtipodocumento;
SELECT * FROM tbtipoOperacion;
SELECT * FROM tbtipoProducto;
SELECT * FROM tbUbicacionGeografica2;
SELECT * FROM tbUbigacionGeografica;