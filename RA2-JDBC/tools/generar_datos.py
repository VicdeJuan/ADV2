"""Genera los datos SQL de clase de forma determinista, sin dependencias externas.

Uso desde RA2-JDBC: python tools/generar_datos.py
Los archivos resultantes se versionan para poder ejecutar el bloque solo con SQL.
"""

from pathlib import Path


RAIZ = Path(__file__).resolve().parents[1]
TAMANO_LOTE = 500


def insertar(archivo, tabla, columnas, filas):
    encabezado = f"INSERT INTO {tabla} ({', '.join(columnas)}) VALUES\n"
    lote = []
    for fila in filas:
        lote.append("  (" + ", ".join(fila) + ")")
        if len(lote) == TAMANO_LOTE:
            archivo.write(encabezado + ",\n".join(lote) + ";\n")
            lote.clear()
    if lote:
        archivo.write(encabezado + ",\n".join(lote) + ";\n")
    archivo.write("\n")


def botanicos():
    especialidades = ("Dendrología", "Ecología", "Fisiología vegetal", "Conservación")
    for id_ in range(1, 8001):
        yield (str(id_), f"'Botánico {id_:04d}'", f"'{especialidades[(id_ - 1) % 4]}'")


def credenciales():
    for id_ in range(1, 5001):
        fecha = f"202{(id_ - 1) % 6}-{(id_ - 1) % 12 + 1:02d}-15"
        yield (str(id_), f"'CRED-{id_:05d}'", f"'{fecha}'", str(id_))


def parcelas():
    for id_ in range(1, 10001):
        yield (str(id_), f"'Parcela {id_:05d}'", f"{50 + (id_ % 900) / 10:.1f}",
               str((id_ - 1) % 7000 + 1))


def arboles():
    especies = ("Roble", "Pino", "Olivo", "Encina", "Arce", "Abedul", "Tejo", "Haya")
    for id_ in range(1, 40001):
        origen = str(id_ - 1) if id_ % 4 == 0 else "NULL"
        yield (str(id_), f"'Árbol {id_:05d}'", f"'{especies[(id_ - 1) % 8]}'",
               str((id_ * 7) % 180), f"{1 + (id_ % 300) / 10:.1f}",
               str((id_ - 1) % 9000 + 1), origen)


def estudios():
    for indice in range(30000):
        botanico_id = indice * 37 % 7000 + 1
        arbol_id = indice % 12000 + 1
        fecha = f"202{indice % 6}-{indice % 12 + 1:02d}-{indice % 28 + 1:02d}"
        observaciones = "NULL" if indice % 5 == 0 else f"'Seguimiento {indice + 1:05d}'"
        yield (str(botanico_id), str(arbol_id), f"'{fecha}'", observaciones)


def generar(motor):
    destino = RAIZ / "sql" / motor / "02-datos.sql"
    with destino.open("w", encoding="utf-8", newline="\n") as archivo:
        archivo.write("-- Datos reproducibles. Ejecutar una sola vez tras 01-esquema.sql.\n")
        archivo.write("-- 8000 botánicos, 5000 credenciales, 10000 parcelas, 40000 árboles y 30000 estudios.\n")
        if motor == "sqlite":
            archivo.write("PRAGMA foreign_keys = ON;\n")
            archivo.write("BEGIN TRANSACTION;\n\n")
        else:
            archivo.write("START TRANSACTION;\n\n")
        insertar(archivo, "botanico", ("id", "nombre", "especialidad"), botanicos())
        insertar(archivo, "credencial", ("id", "codigo", "fecha_emision", "botanico_id"), credenciales())
        insertar(archivo, "parcela", ("id", "nombre", "superficie", "botanico_id"), parcelas())
        insertar(archivo, "arbol", ("id", "nombre", "especie", "edad", "altura",
                                    "parcela_id", "arbol_origen_id"), arboles())
        insertar(archivo, "estudio", ("botanico_id", "arbol_id", "fecha_inicio",
                                      "observaciones"), estudios())
        archivo.write("COMMIT;\n")
    print(destino.relative_to(RAIZ))


if __name__ == "__main__":
    generar("sqlite")
    generar("mysql")
