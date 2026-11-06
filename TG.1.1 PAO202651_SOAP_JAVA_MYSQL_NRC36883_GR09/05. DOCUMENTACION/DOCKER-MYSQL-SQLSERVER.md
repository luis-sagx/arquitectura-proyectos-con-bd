# Base de datos local con Docker Compose

El archivo `docker-compose.mysql.yml` de la raíz del proyecto levanta la base MySQL a la que se conecta el servidor EurekaBank.

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

El dataset incluye credenciales de ejemplo almacenadas como SHA-1; el login del servidor compara ese formato y solo acepta usuarios activos. El usuario de prueba es `Monster` / `Monster9`; es la única cuenta activa del dataset.

## Detener el contenedor

```powershell
docker compose -f docker-compose.mysql.yml down
```

El volumen se conserva con `down`. Para borrarlo junto con los datos, usa `down -v`.
