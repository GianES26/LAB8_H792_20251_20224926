CREATE DATABASE IF NOT EXISTS Lab8_20224926;
USE Lab8_20224926;

CREATE TABLE monitoreo_climatico (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ciudad VARCHAR(50) NOT NULL,
    fecha DATE NOT NULL,
    temperatura_promedio DECIMAL(5,2) NOT NULL,
    condicion_frecuente VARCHAR(100) NOT NULL,
    temperatura_maxima DECIMAL(5,2) NOT NULL,
    temperatura_minima DECIMAL(5,2) NOT NULL,
    fecha_registro DATETIME NOT NULL
);