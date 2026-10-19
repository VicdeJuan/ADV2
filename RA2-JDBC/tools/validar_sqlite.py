"""Comprueba esquema, datos y restricciones del bloque 1 con SQLite de Python.

Es una herramienta de validación SQL, no un ejemplo JDBC para el alumnado.
Uso desde RA2-JDBC: python tools/validar_sqlite.py
"""

from pathlib import Path
import sqlite3


RAIZ = Path(__file__).resolve().parents[1]


def valor(conexion, sql):
    return conexion.execute(sql).fetchone()[0]


def debe_fallar(conexion, sql, parametros):
    try:
        conexion.execute(sql, parametros)
    except sqlite3.IntegrityError:
        return
    raise AssertionError(f"Se aceptó una fila inválida: {sql}")


def main():
    with sqlite3.connect(":memory:") as conexion:
        conexion.execute("PRAGMA foreign_keys = ON")
        assert valor(conexion, "PRAGMA foreign_keys") == 1
        for nombre in ("01-esquema.sql", "02-datos.sql"):
            sql = (RAIZ / "sql" / "sqlite" / nombre).read_text(encoding="utf-8")
            conexion.executescript(sql)

        esperados = {"botanico": 8000, "credencial": 5000, "parcela": 10000,
                     "arbol": 40000, "estudio": 30000}
        for tabla, esperado in esperados.items():
            real = valor(conexion, f"SELECT count(*) FROM {tabla}")
            assert real == esperado, (tabla, real, esperado)

        comprobaciones = {
            "botánicos sin credencial": "SELECT count(*) FROM botanico b LEFT JOIN credencial c ON c.botanico_id=b.id WHERE c.id IS NULL",
            "botánicos sin parcela": "SELECT count(*) FROM botanico b LEFT JOIN parcela p ON p.botanico_id=b.id WHERE p.id IS NULL",
            "parcelas sin árbol": "SELECT count(*) FROM parcela p LEFT JOIN arbol a ON a.parcela_id=p.id WHERE a.id IS NULL",
            "árboles sin estudio": "SELECT count(*) FROM arbol a LEFT JOIN estudio e ON e.arbol_id=a.id WHERE e.arbol_id IS NULL",
            "árboles sin origen": "SELECT count(*) FROM arbol WHERE arbol_origen_id IS NULL",
            "árboles con origen": "SELECT count(*) FROM arbol WHERE arbol_origen_id IS NOT NULL",
            "árboles con varios estudios": "SELECT count(*) FROM (SELECT arbol_id FROM estudio GROUP BY arbol_id HAVING count(*) > 1)",
            "árboles origen": "SELECT count(DISTINCT arbol_origen_id) FROM arbol WHERE arbol_origen_id IS NOT NULL",
        }
        for caso, sql in comprobaciones.items():
            n = valor(conexion, sql)
            assert n > 0, caso
            print(f"{caso}: {n}")

        assert conexion.execute("PRAGMA foreign_key_check").fetchall() == []
        conexion.execute("SAVEPOINT restricciones")
        debe_fallar(conexion, "INSERT INTO botanico VALUES (?, ?, ?)",
                    (-1, "Inválido", "Ecología"))
        debe_fallar(conexion, "INSERT INTO botanico VALUES (?, ?, ?)",
                    (8001, "  ", "Ecología"))
        debe_fallar(conexion, "INSERT INTO credencial VALUES (?, ?, ?, ?)",
                    (5001, "CRED-NUEVA", "2026-10-15", 1))
        debe_fallar(conexion, "INSERT INTO parcela VALUES (?, ?, ?, ?)",
                    (10001, "Parcela inválida", 0, 1))
        debe_fallar(conexion, "INSERT INTO arbol VALUES (?, ?, ?, ?, ?, ?, ?)",
                    (40001, "Árbol inválido", "Roble", 1, 2.0, 99999, None))
        debe_fallar(conexion, "INSERT INTO arbol VALUES (?, ?, ?, ?, ?, ?, ?)",
                    (40001, "Árbol inválido", "Roble", 1, 2.0, 1, 40001))
        debe_fallar(conexion, "INSERT INTO arbol VALUES (?, ?, ?, ?, ?, ?, ?)",
                    (40001, "Árbol inválido", "Roble", -1, 2.0, 1, None))
        debe_fallar(conexion, "INSERT INTO estudio VALUES (?, ?, ?, ?)",
                    (1, 1, "2026-10-15", None))
        conexion.execute("ROLLBACK TO restricciones")
        conexion.execute("RELEASE restricciones")
        print("Recuentos, casos JOIN, claves foráneas y restricciones: correctos")


if __name__ == "__main__":
    main()
