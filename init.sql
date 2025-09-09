-- Creación de la base de datos (opcional, depende de tu configuración)
CREATE DATABASE IF NOT EXISTS gestion_horarios;
USE gestion_horarios;

-- Tabla USUARIO (versión mínima)
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL,
    cambiar_pass BOOLEAN NOT NULL
) ENGINE=InnoDB;


