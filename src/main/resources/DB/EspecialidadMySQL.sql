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

INSERT INTO Especialidad (nombre) VALUES
    ("Cirugia"),
    ("Cardiologia"),
    ("Urologia"),
    ("Radiografia"),
    ("Pediatria"),
    ("Dermatologia"),
    ("Neurologia"),
    ("Oftalmologia"),
    ("Traumatologia"),
    ("Ginecologia"),
    ("Endocrinologia"),
    ("Psiquiatria"),
    ("Reumatologia"),
    ("Oncologia"),
    ("Neumologia"),
    ("Otorrinolaringologia"),
    ("Medicina General"),
    ("Fisioterapia"),
    ("Nutricion"),
    ("Anestesiologia");

select * from Especialidad where True;