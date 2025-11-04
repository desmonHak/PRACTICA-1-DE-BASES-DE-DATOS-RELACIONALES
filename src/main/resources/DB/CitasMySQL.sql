-- crear una base de datos nuevas si no existe
CREATE DATABASE IF NOT EXISTS PRACTICA_1_DE_BASES_DE_DATOS_RELACIONALES;

-- indicar que se usa la nueva DB
USE PRACTICA_1_DE_BASES_DE_DATOS_RELACIONALES;


-- DROP TABLE Citas;

-- crear la tabla para Citas
CREATE TABLE IF NOT EXISTS Citas (
    dni 			VARCHAR(9),
    numero_cita 	int not null PRIMARY KEY AUTO_INCREMENT,
    fecha_cita 		date not null,
    especialidad	VARCHAR(64) not null,
	FOREIGN KEY (dni) REFERENCES Paciente(dni) ON DELETE CASCADE,
    FOREIGN KEY (especialidad) REFERENCES Especialidad(nombre)  ON DELETE CASCADE
)  	ENGINE = InnoDB 
	DEFAULT CHARSET = utf8mb4 
    AUTO_INCREMENT = 1;

INSERT INTO Citas (
	dni, numero_cita, 
    fecha_cita, especialidad) VALUES ('48901940F', 1, '2025-10-20', 'Cirugia'),
                                     ('49234567P', 3, '2025-10-23', 'Urologia'),
                                     ('48345678T', 4, '2025-10-25', 'Radiografia'),
                                     ('47456789S', 5, '2025-10-27', 'Cardiologia'),
                                     ('46567890C', 6, '2025-11-01', 'Cirugia'),
                                     ('45678901G', 7, '2025-11-03', 'Radiografia'),
                                     ('44789012P', 8, '2025-11-05', 'Urologia'),
                                     ('44789012P', 9, '2025-11-10', 'Cardiologia'),
                                     ('42901234R', 10, '2025-11-15', 'Cirugia'),
                                     ('41912345C', 11, '2025-11-18', 'Radiografia');
    
Select * from citas;