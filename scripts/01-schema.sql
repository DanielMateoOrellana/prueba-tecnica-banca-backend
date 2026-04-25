-- Schema para la Prueba Tecnica - Banca
-- Compatible con MySQL 8 + Hibernate (ddl-auto=update lo respetara si las tablas ya existen)

CREATE DATABASE IF NOT EXISTS banca
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE banca;

CREATE TABLE IF NOT EXISTS cliente (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(120) NOT NULL,
    genero          VARCHAR(20)  NOT NULL,
    edad            INT          NOT NULL,
    identificacion  VARCHAR(30)  NOT NULL UNIQUE,
    direccion       VARCHAR(200) NOT NULL,
    telefono        VARCHAR(30)  NOT NULL,
    cliente_id      VARCHAR(30)  NOT NULL UNIQUE,
    contrasena      VARCHAR(100) NOT NULL,
    estado          BOOLEAN      NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cuenta (
    numero_cuenta   VARCHAR(30)    NOT NULL PRIMARY KEY,
    tipo_cuenta     VARCHAR(20)    NOT NULL,
    saldo_inicial   DECIMAL(19,2)  NOT NULL,
    saldo_actual    DECIMAL(19,2)  NOT NULL,
    estado          BOOLEAN        NOT NULL DEFAULT TRUE,
    cliente_id_fk   BIGINT         NOT NULL,
    CONSTRAINT fk_cuenta_cliente FOREIGN KEY (cliente_id_fk) REFERENCES cliente (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS movimiento (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha            DATETIME       NOT NULL,
    tipo_movimiento  VARCHAR(20)    NOT NULL,
    valor            DECIMAL(19,2)  NOT NULL,
    saldo            DECIMAL(19,2)  NOT NULL,
    numero_cuenta    VARCHAR(30)    NOT NULL,
    CONSTRAINT fk_movimiento_cuenta FOREIGN KEY (numero_cuenta) REFERENCES cuenta (numero_cuenta) ON DELETE CASCADE,
    INDEX idx_movimiento_cuenta_fecha (numero_cuenta, fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
