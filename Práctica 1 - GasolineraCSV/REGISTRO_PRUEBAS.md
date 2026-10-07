# Registro de pruebas

| Caso | Pasos y datos | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| Primer arranque | Ejecutar sin `data/` | Se crean ambos CSV con cabecera y se muestra el menú. |  |
| Campos vacíos | Alta: nombre vacío; pago: combustible vacío | Se vuelve a pedir solo el campo obligatorio. |  |
| Matrícula duplicada | Alta `1234abc`; después `1234ABC` | Se rechaza la segunda y no se añade fila. |  |
| Búsqueda | Buscar `aBc` y luego `inexistente` | Encuentra por matrícula sin distinguir caso; después informa de lista vacía. |  |
| Pago sin clientes | Primer arranque, opción 4 | Informa de que antes hay que dar de alta un cliente. |  |
| Cliente inexistente | Con cliente 1, pago para ID 99 | No registra fila en `pagos.csv`. |  |
| Fecha imposible | Introducir `31/02/2026` | Rechaza la fecha y la vuelve a pedir. |  |
| Cantidades inválidas | `-5`, `1.234`, `abc`; finalmente `40,50` y `25` | Rechaza las tres primeras y acepta coma o punto con hasta dos decimales. |  |
| Listados | Crear Ana, Luis y pagos de fechas distintas | Clientes por nombre; pagos por fecha e ID descendentes; dos decimales. |  |
| Persistencia | Salir, ejecutar de nuevo y crear otro registro | Se recuperan datos; nuevos IDs continúan desde el máximo. |  |
| CSV con texto especial | Nombre `Ana, "A"` y combustible con comillas | Al reiniciar se recuperan exactamente los textos. |  |
| Fichero malformado | Añadir una fila con tres columnas a `clientes.csv` | Informa de fila incorrecta, finaliza y no reescribe el fichero. |  |
