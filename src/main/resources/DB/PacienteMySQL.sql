-- crear una base de datos nuevas si no existe
CREATE DATABASE IF NOT EXISTS PRACTICA_1_DE_BASES_DE_DATOS_RELACIONALES;

-- indicar que se usa la nueva DB
USE PRACTICA_1_DE_BASES_DE_DATOS_RELACIONALES;

-- delete from Paciente where DNI ='';
-- DROP TABLE Paciente;

-- crear la tabla para Paciente
CREATE TABLE IF NOT EXISTS Paciente (
	email 			TEXT,
    password 		VARCHAR(64),
    DNI 			VARCHAR(9) PRIMARY KEY,
    nombre 			text not null,
    direccion 		text,
    telefono 		text
);

INSERT INTO Paciente (
	email, password, DNI, nombre, 
    direccion, telefono) VALUES (
		'admin', SHA2('1234', 256), '48901940F','Pedro',
        'C/ salvador', '645645373'
    );
    
select * from Paciente;