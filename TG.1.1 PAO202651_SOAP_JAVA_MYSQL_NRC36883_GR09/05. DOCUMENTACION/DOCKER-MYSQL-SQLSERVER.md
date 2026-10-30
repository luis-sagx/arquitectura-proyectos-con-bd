# Bases de datos locales con Docker Compose

Hay un archivo Compose en la raíz del proyecto para cada motor. El servidor EurekaBank se conecta a **MySQL**; SQL Server es una instancia separada para prácticas y no es un backend configurado de la aplicación.

## MySQL con esquema y datos EurekaBank

En la raíz del proyecto, ejecuta en PowerShell:

```powershell
docker compose -f docker-compose.mysql.yml up -d
docker compose -f docker-compose.mysql.yml ps
docker compose -f docker-compose.mysql.yml logs -f mysql
```

El contenedor inicializa `eurekabank` con el usuario `monster` y contraseña `monster9`. Los scripts de esquema y datos vienen del directorio MySQL de [gcoronelc/databases/EurekaBank](https://github.com/gcoronelc/databases/tree/master/EurekaBank/MySQL/Modelo). Se adaptó el script de esquema para omitir `DROP DATABASE` y los permisos globales del usuario `eureka`; Compose crea la base y la cuenta limitada `monster`.

Los archivos están en `03. DBB/mysql/init/` y Docker los ejecuta por nombre: primero `01-eurekabank-schema.sql` y después `02-eurekabank-data.sql`. La inicialización ocurre **solo cuando el volumen MySQL está vacío**. Para volver a cargar el esquema y los datos desde cero, elimina conscientemente el volumen y crea el contenedor otra vez:

```powershell
docker compose -f docker-compose.mysql.yml down -v
docker compose -f docker-compose.mysql.yml up -d
```

`down -v` elimina todos los datos guardados por ese contenedor.

La conexión por defecto del servidor Java es:

```text
DB_HOST=localhost
DB_PORT=3307
DB_NAME=eurekabank
DB_USER=monster
DB_PASSWORD=monster9
```

El dataset incluye credenciales de ejemplo almacenadas como SHA-1; el login del servidor compara ese formato y solo acepta usuarios activos. Por ejemplo, el dataset del repositorio incluye `cromero` / `chicho`.

## SQL Server 2022

```powershell
docker compose -f docker-compose.sqlserver.yml up -d
docker compose -f docker-compose.sqlserver.yml ps
docker compose -f docker-compose.sqlserver.yml logs -f sqlserver
```

El servidor publica el puerto `1433`, usa el usuario administrador `sa` y la contraseña `EurekaDev_2026!`. Sus datos quedan en un volumen separado. Esta configuración no cambia la conexión JDBC de la aplicación, que sigue usando MySQL.

## Detener los contenedores

```powershell
docker compose -f docker-compose.mysql.yml down
docker compose -f docker-compose.sqlserver.yml down
```

Los volúmenes se conservan con `down`. Para borrarlos junto con los datos, usa `down -v` en el Compose correspondiente.
