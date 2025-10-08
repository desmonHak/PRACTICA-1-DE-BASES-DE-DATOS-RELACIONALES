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
    ),
    ('maria.gomez@example.com', SHA2('maria123', 256), '50123456A', 'María Gómez', 'Av. de la Constitución 23, Madrid', '612345678'),
    ('juan.lopez@example.com', SHA2('juanpass', 256), '49234567B', 'Juan López', 'C/ Mayor 45, Valencia', '698765432'),
    ('laura.martin@example.com', SHA2('laura2024', 256), '48345678C', 'Laura Martín', 'C/ Prado 7, Sevilla', '677889900'),
    ('carlos.perez@example.com', SHA2('carlitos', 256), '47456789D', 'Carlos Pérez', 'C/ Real 19, Bilbao', '655443322'),
    ('ana.ruiz@example.com', SHA2('ana456', 256), '46567890E', 'Ana Ruiz', 'Plaza del Sol 8, Zaragoza', '699112233'),
    ('jose.moreno@example.com', SHA2('jose789', 256), '45678901F', 'José Moreno', 'Av. Libertad 12, Málaga', '600998877'),
    ('lucia.sanchez@example.com', SHA2('lucia321', 256), '44789012G', 'Lucía Sánchez', 'C/ Luna 30, Murcia', '634556677'),
    ('david.torres@example.com', SHA2('davidpass', 256), '43890123H', 'David Torres', 'C/ Jardines 2, Valladolid', '622334455'),
    ('marta.fernandez@example.com', SHA2('marta2025', 256), '42901234J', 'Marta Fernández', 'C/ Colón 15, Alicante', '688990011'),
    ('santiago.romero@example.com', SHA2('santi987', 256), '41912345K', 'Santiago Romero', 'Paseo del Río 9, Córdoba', '611223344');
;
    
select * from Paciente;