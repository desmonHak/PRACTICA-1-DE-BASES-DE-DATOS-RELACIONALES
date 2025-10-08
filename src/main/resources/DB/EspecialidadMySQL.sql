-- crear una base de datos nuevas si no existe
CREATE DATABASE IF NOT EXISTS PRACTICA_1_DE_BASES_DE_DATOS_RELACIONALES;

-- indicar que se usa la nueva DB
USE PRACTICA_1_DE_BASES_DE_DATOS_RELACIONALES;

-- delete from Paciente where DNI ='';
-- DROP TABLE Especialidad;

-- crear la tabla para Especialidad
CREATE TABLE IF NOT EXISTS Especialidad (
	nombre TEXT
);

INSERT INTO Especialidad values
	("Cirugia"),
    ("Cardiologia"),
    ("Urologia"),
    ("Radiografia");

select * from Especialidad;