-- Ejecutar en una base de datos de prácticas nueva, seleccionada previamente.
-- Diseñado para MySQL 8.4+ con InnoDB; validado también en MariaDB 10.4.32.
-- No borra tablas existentes.

CREATE TABLE botanico (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    especialidad VARCHAR(120) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_botanico_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) > 0),
    CONSTRAINT chk_botanico_especialidad CHECK (CHAR_LENGTH(TRIM(especialidad)) > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE credencial (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(32) NOT NULL,
    fecha_emision DATE NOT NULL,
    botanico_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_credencial_codigo UNIQUE (codigo),
    CONSTRAINT uq_credencial_botanico UNIQUE (botanico_id),
    CONSTRAINT chk_credencial_codigo CHECK (CHAR_LENGTH(TRIM(codigo)) > 0),
    CONSTRAINT fk_credencial_botanico FOREIGN KEY (botanico_id) REFERENCES botanico(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE parcela (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    superficie DOUBLE NOT NULL,
    botanico_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    KEY idx_parcela_botanico (botanico_id),
    CONSTRAINT chk_parcela_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) > 0),
    CONSTRAINT chk_parcela_superficie CHECK (superficie > 0),
    CONSTRAINT fk_parcela_botanico FOREIGN KEY (botanico_id) REFERENCES botanico(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE arbol (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    especie VARCHAR(120) NOT NULL,
    edad INT UNSIGNED NOT NULL,
    altura DOUBLE NOT NULL,
    parcela_id INT UNSIGNED NOT NULL,
    arbol_origen_id INT UNSIGNED NULL,
    PRIMARY KEY (id),
    KEY idx_arbol_parcela (parcela_id),
    KEY idx_arbol_origen (arbol_origen_id),
    KEY idx_arbol_especie (especie),
    CONSTRAINT chk_arbol_nombre CHECK (CHAR_LENGTH(TRIM(nombre)) > 0),
    CONSTRAINT chk_arbol_especie CHECK (CHAR_LENGTH(TRIM(especie)) > 0),
    CONSTRAINT chk_arbol_altura CHECK (altura > 0),
    CONSTRAINT fk_arbol_parcela FOREIGN KEY (parcela_id) REFERENCES parcela(id),
    CONSTRAINT fk_arbol_origen FOREIGN KEY (arbol_origen_id) REFERENCES arbol(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- MySQL no permite que un CHECK mencione una columna AUTO_INCREMENT.
-- En un BEFORE INSERT, NEW.id vale 0 si MySQL aún debe generar el ID;
-- un origen válido siempre apunta a una fila anterior con ID positivo.
DELIMITER $$
CREATE TRIGGER trg_arbol_origen_insert
BEFORE INSERT ON arbol FOR EACH ROW
BEGIN
    IF NEW.arbol_origen_id IS NOT NULL AND NEW.arbol_origen_id = NEW.id THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Un árbol no puede ser su propio origen';
    END IF;
END$$

CREATE TRIGGER trg_arbol_origen_update
BEFORE UPDATE ON arbol FOR EACH ROW
BEGIN
    IF NEW.arbol_origen_id IS NOT NULL AND NEW.arbol_origen_id = NEW.id THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Un árbol no puede ser su propio origen';
    END IF;
END$$
DELIMITER ;

CREATE TABLE estudio (
    botanico_id INT UNSIGNED NOT NULL,
    arbol_id INT UNSIGNED NOT NULL,
    fecha_inicio DATE NOT NULL,
    observaciones TEXT NULL,
    PRIMARY KEY (botanico_id, arbol_id),
    KEY idx_estudio_arbol (arbol_id),
    CONSTRAINT fk_estudio_botanico FOREIGN KEY (botanico_id) REFERENCES botanico(id),
    CONSTRAINT fk_estudio_arbol FOREIGN KEY (arbol_id) REFERENCES arbol(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
