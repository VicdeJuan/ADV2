plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    runtimeOnly("org.xerial:sqlite-jdbc:3.53.4.0")
    runtimeOnly("com.mysql:mysql-connector-j:26.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.1.3")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

val paqueteEjemplos = "es.iesluisbraille.accesodatos.jardin.bloque02"

listOf(
    "runSqlite" to "PrimeraConexionSqlite",
    "runSqliteMetadata" to "MetadatosSqlite",
    "runMysql" to "PrimeraConexionMysql",
    "runErrorConexion" to "ErrorDeConexion"
).forEach { (nombreTarea, nombreClase) ->
    tasks.register<JavaExec>(nombreTarea) {
        group = "application"
        description = "Ejecuta el ejemplo $nombreClase del bloque 2"
        classpath = sourceSets.main.get().runtimeClasspath
        mainClass.set("$paqueteEjemplos.$nombreClase")
    }
}
