"""Crea una base SQLite de clase nueva a partir de los scripts del bloque 1."""

from pathlib import Path
import sqlite3
import sys


RAIZ = Path(__file__).resolve().parents[1]


def main():
    destino = Path(sys.argv[1]) if len(sys.argv) > 1 else RAIZ / "data" / "jardin-botanico.db"
    destino = destino.resolve()
    if destino.exists():
        raise SystemExit(f"La base ya existe y no se sobrescribirá: {destino}")
    destino.parent.mkdir(parents=True, exist_ok=True)
    try:
        with sqlite3.connect(destino) as conexion:
            for nombre in ("01-esquema.sql", "02-datos.sql"):
                sql = (RAIZ / "sql" / "sqlite" / nombre).read_text(encoding="utf-8")
                conexion.executescript(sql)
            if conexion.execute("PRAGMA foreign_key_check").fetchone() is not None:
                raise ValueError("Los datos contienen una referencia inválida")
        print(f"Base creada: {destino}")
    except Exception:
        destino.unlink(missing_ok=True)
        raise


if __name__ == "__main__":
    main()
