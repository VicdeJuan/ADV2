# Solución de referencia — solo profesorado

Este proyecto **sí contiene la solución** de los seis ejercicios. No lo compartas con el alumnado.

Es exactamente la misma estructura que el proyecto del alumno, con la implementación de los métodos en `ExamenRA1.java`.

Abre la carpeta en IntelliJ con Java 17+ o compila desde la raíz:

```sh
mkdir -p out
javac -encoding UTF-8 -d out src/main/java/es/iesluisbraille/accesodatos/*.java
java -cp out es.iesluisbraille.accesodatos.ExamenRA1
```

Introduce como ruta de salida `datos/salida`.

Resultados esperados: 200 alumnos del fichero 2; 45 del fichero 3; 240 fusionados antes de añadir el alumno 1; 241 tras añadirlo. El fichero binario contiene los cuatro bytes `00 00 00 C8`.

El `alumnos_ordenados.csv` no debe cambiar si vuelves a ejecutar sin borrarlo.
