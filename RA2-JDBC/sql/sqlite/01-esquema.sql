-- Ejecutar en una base de datos nueva. Activar las FK en CADA conexión.
PRAGMA foreign_keys = ON;

CREATE TABLE botanico (
    id INTEGER PRIMARY KEY CHECK (id > 0),
    nombre TEXT NOT NULL CHECK (length(trim(nombre)) > 0),
    especialidad TEXT NOT NULL CHECK (length(trim(especialidad)) > 0)
) STRICT;

CREATE TABLE credencial (
    id INTEGER PRIMARY KEY CHECK (id > 0),
    codigo TEXT NOT NULL UNIQUE CHECK (length(trim(codigo)) > 0),
    fecha_emision TEXT NOT NULL CHECK (length(fecha_emision) = 10
        AND fecha_emision GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'),
    botanico_id INTEGER NOT NULL UNIQUE,
    FOREIGN KEY (botanico_id) REFERENCES botanico(id)
) STRICT;

CREATE TABLE parcela (
    id INTEGER PRIMARY KEY CHECK (id > 0),
    nombre TEXT NOT NULL CHECK (length(trim(nombre)) > 0),
    superficie REAL NOT NULL CHECK (superficie > 0),
    botanico_id INTEGER NOT NULL,
    FOREIGN KEY (botanico_id) REFERENCES botanico(id)
) STRICT;

CREATE INDEX idx_parcela_botanico ON parcela(botanico_id);

CREATE TABLE arbol (
    id INTEGER PRIMARY KEY CHECK (id > 0),
    nombre TEXT NOT NULL CHECK (length(trim(nombre)) > 0),
    especie TEXT NOT NULL CHECK (length(trim(especie)) > 0),
    edad INTEGER NOT NULL CHECK (edad >= 0),
    altura REAL NOT NULL CHECK (altura > 0),
    parcela_id INTEGER NOT NULL,
    arbol_origen_id INTEGER,
    CHECK (arbol_origen_id IS NULL OR arbol_origen_id <> id),
    FOREIGN KEY (parcela_id) REFERENCES parcela(id),
    FOREIGN KEY (arbol_origen_id) REFERENCES arbol(id)
) STRICT;

CREATE INDEX idx_arbol_parcela ON arbol(parcela_id);
CREATE INDEX idx_arbol_origen ON arbol(arbol_origen_id);
CREATE INDEX idx_arbol_especie ON arbol(especie);

CREATE TABLE estudio (
    botanico_id INTEGER NOT NULL,
    arbol_id INTEGER NOT NULL,
    fecha_inicio TEXT NOT NULL CHECK (length(fecha_inicio) = 10
        AND fecha_inicio GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'),
    observaciones TEXT,
    PRIMARY KEY (botanico_id, arbol_id),
    FOREIGN KEY (botanico_id) REFERENCES botanico(id),
    FOREIGN KEY (arbol_id) REFERENCES arbol(id)
) STRICT;

CREATE INDEX idx_estudio_arbol ON estudio(arbol_id);
