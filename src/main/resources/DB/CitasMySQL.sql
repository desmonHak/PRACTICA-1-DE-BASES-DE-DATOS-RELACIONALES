-- crear una base de datos nuevas si no existe
CREATE DATABASE IF NOT EXISTS PRACTICA_1_DE_BASES_DE_DATOS_RELACIONALES;

-- indicar que se usa la nueva DB
USE PRACTICA_1_DE_BASES_DE_DATOS_RELACIONALES;


-- DROP TABLE Citas;

-- crear la tabla para Citas
CREATE TABLE IF NOT EXISTS Citas (
    DNI 			VARCHAR(9),
    numero_cita 	int not null PRIMARY KEY,
    fecha_cita 		date not null,
    especialidad	text not null
);

INSERT INTO Citas (
	DNI, numero_cita, 
    fecha_cita, especialidad) VALUES (
		'48901940F', 1,
        '2025-10-20', 
        'Cirugia'
    );
    
Select * from citas;