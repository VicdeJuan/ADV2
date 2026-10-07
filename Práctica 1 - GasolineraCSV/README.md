# Práctica 2 — Gasolinera


## 1. Decisiones sobre las clases


### ¿Por qué separar `ServicioGasolinera` de `AplicacionConsola`?

`AplicacionConsola` se ocupa de la interacción con la persona usuaria: muestra
el menú, solicita datos mediante teclado y presenta los resultados.

`ServicioGasolinera` se ocupa de las reglas de la aplicación: comprobar que una
matrícula no esté repetida, asignar identificadores, buscar, ordenar y registrar
pagos. También pide el guardado mediante la interfaz `Almacenamiento`.

Por ejemplo, al dar de alta un cliente, la consola recoge el nombre, el teléfono
y la matrícula. Después, el servicio comprueba la matrícula y solicita que se
guarde el cliente. Esta separación evita mezclar `Scanner` y `System.out` con
las reglas del negocio, y permitiría reutilizar el servicio con una interfaz
gráfica o web.

### ¿Se podrían fusionar `Main` y `AplicacionConsola`?

Sí. En una aplicación pequeña se podrían fusionar sin que fuese incorrecto.
Actualmente `Main` solo crea los objetos necesarios y arranca el programa:

```java
Almacenamiento almacenamiento = new AlmacenamientoCsv(Path.of("data"));
ServicioGasolinera servicio = new ServicioGasolinera(almacenamiento);
new AplicacionConsola(servicio).ejecutar();
```

`AplicacionConsola` contiene el menú. Mantenerlas separadas hace más visible el
punto de inicio y evita que la consola tenga que decidir el tipo de
almacenamiento. No es una separación obligatoria para esta práctica, sino una
decisión de organización del código.

## 2. ¿Qué ocurre si hay errores al leer un archivo?

Al iniciar, `AlmacenamientoCsv` lee los dos ficheros con `BufferedReader` y
comprueba que tengan la cabecera esperada y que cada registro pueda interpretarse.
También se comprueban los identificadores, fechas, cantidades y textos
obligatorios. Después, `ServicioGasolinera` verifica que no haya identificadores
o matrículas duplicadas y que cada pago pertenezca a un cliente existente.

Si falta el fichero, no se puede acceder a él, el CSV está mal formado o algún
dato no es válido, se lanza `ErrorAlmacenamiento`. `Main` captura ese error,
muestra un mensaje comprensible y finaliza de forma controlada. No se sobrescribe
ni se descarta silenciosamente el fichero problemático.

Si el error ocurre durante un alta o un pago (por ejemplo, al intentar escribir),
la consola también muestra el error y termina. Como el servicio añade el nuevo
objeto a su lista solo después de que el almacenamiento confirme la escritura, no
se anuncia como guardada una operación que ha fallado.

## 3. Excepciones creadas
Se puede crear un tipo de excepción pero lanzarla de formas diferentes.

