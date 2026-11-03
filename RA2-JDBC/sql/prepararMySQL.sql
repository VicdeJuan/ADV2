-- Este código es para ejecutar con un usuario de mysql con permisos.
select * from mysql.user;

CREATE USER 'botanico'@'localhost' IDENTIFIED BY '1234'; -- creamos un usuario

GRANT ALL PRIVILEGES ON ra2_botanico.* TO 'botanico'@'localhost'; 
-- le damos todos los permisos pero SOLO para la BBDD del RA2.

-- ra2_botanico es la BBDD

show databases;
use ra2_botanico; -- todavía no existe. DA ERROR.

CREATE DATABASE IF NOT EXISTS ra2_botanico;
