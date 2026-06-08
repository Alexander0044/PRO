

CREATE DATABASE IF NOT EXISTS ecotrack
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ecotrack;

CREATE TABLE IF NOT EXISTS grupos (
    id          INT          NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    docente     VARCHAR(100),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS alumnos (
    id          INT          NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL,
    apellidos   VARCHAR(100) NOT NULL,
    email       VARCHAR(150),
    matricula   VARCHAR(20)  NOT NULL UNIQUE,
    grupo_id    INT,
    PRIMARY KEY (id),
    FOREIGN KEY (grupo_id) REFERENCES grupos(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS categorias_impacto (
    id          INT          NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tipos_impacto (
    id           INT          NOT NULL AUTO_INCREMENT,
    nombre       VARCHAR(100) NOT NULL,
    unidad       VARCHAR(50)  NOT NULL,
    categoria_id INT          NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (categoria_id) REFERENCES categorias_impacto(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS factores_emision (
    id             INT            NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(150)   NOT NULL,
    valor          DOUBLE         NOT NULL,
    tipo_impacto_id INT           NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (tipo_impacto_id) REFERENCES tipos_impacto(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS registros_actividad (
    id                    INT    NOT NULL AUTO_INCREMENT,
    alumno_id             INT    NOT NULL,
    fecha                 DATE   NOT NULL,
    creado_en             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    consumo_electricidad  DOUBLE NOT NULL DEFAULT 0,
    consumo_gas_natural   DOUBLE NOT NULL DEFAULT 0,
    km_coche_privado      DOUBLE NOT NULL DEFAULT 0,
    km_transporte_publico DOUBLE NOT NULL DEFAULT 0,
    vuelos_cortos         DOUBLE NOT NULL DEFAULT 0,
    vuelos_largos         DOUBLE NOT NULL DEFAULT 0,
    consumo_carne         DOUBLE NOT NULL DEFAULT 0,
    residuos_kg           DOUBLE NOT NULL DEFAULT 0,
    huella_total          DOUBLE,
    PRIMARY KEY (id),
    FOREIGN KEY (alumno_id) REFERENCES alumnos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS resultados_calculo (
    id                    INT    NOT NULL AUTO_INCREMENT,
    alumno_id             INT    NOT NULL,
    fecha                 DATE   NOT NULL,
    huella_carbono        DOUBLE NOT NULL,
    huella_electricidad   DOUBLE NOT NULL DEFAULT 0,
    huella_gas_natural    DOUBLE NOT NULL DEFAULT 0,
    huella_mov_privada    DOUBLE NOT NULL DEFAULT 0,
    huella_mov_publica    DOUBLE NOT NULL DEFAULT 0,
    huella_vuelos         DOUBLE NOT NULL DEFAULT 0,
    huella_alimentacion   DOUBLE NOT NULL DEFAULT 0,
    huella_residuos       DOUBLE NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    FOREIGN KEY (alumno_id) REFERENCES alumnos(id) ON DELETE CASCADE
);