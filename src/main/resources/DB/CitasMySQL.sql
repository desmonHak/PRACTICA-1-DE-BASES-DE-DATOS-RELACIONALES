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
    fecha_cita, especialidad) VALUES ('48901940F', 1, '2025-10-20', 'Cirugia'),
                                     ('50123456A', 2, '2025-10-22', 'Cardiologia'),
                                     ('49234567B', 3, '2025-10-23', 'Urologia'),
                                     ('48345678C', 4, '2025-10-25', 'Radiografia'),
                                     ('47456789D', 5, '2025-10-27', 'Cardiologia'),
                                     ('46567890E', 6, '2025-11-01', 'Cirugia'),
                                     ('45678901F', 7, '2025-11-03', 'Radiografia'),
                                     ('44789012G', 8, '2025-11-05', 'Urologia'),
                                     ('43890123H', 9, '2025-11-10', 'Cardiologia'),
                                     ('42901234J', 10, '2025-11-15', 'Cirugia'),
                                     ('41912345K', 11, '2025-11-18', 'Radiografia');
    
Select * from citas;