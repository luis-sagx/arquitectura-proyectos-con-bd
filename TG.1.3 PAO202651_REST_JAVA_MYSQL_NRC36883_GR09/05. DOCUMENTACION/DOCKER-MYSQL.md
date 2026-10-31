# MySQL local con Docker Compose

Desde la raíz `TG.1.3 PAO202651_REST_JAVA_MYSQL_NRC36883_GR09`, copia `.env.example` a `.env`, configura la clave root y ejecuta:

```powershell
docker compose -f docker-compose.mysql.yml up -d
docker compose -f docker-compose.mysql.yml ps
docker compose -f docker-compose.mysql.yml logs -f mysql
```

La instancia Docker REST publica MySQL en el puerto local `3308`, dejando libre el `3307` SOAP y el `3306` de la instalación MySQL local. El servidor Java usa por defecto `localhost:3306`, base `eurekabank`, usuario `monster`. Para conectarlo al contenedor, establece `$env:DB_PORT = "3308"` en la terminal desde la que arrancas Payara. También puedes cambiar `DB_HOST`, `DB_NAME`, `DB_USER` y `DB_PASSWORD`.

Los archivos de `03. DBB/mysql/init/` se ejecutan en orden solo cuando el volumen está vacío. `docker compose -f docker-compose.mysql.yml down` detiene el contenedor y conserva los datos. `down -v` también elimina permanentemente el volumen REST.

El dataset y las contraseñas son de demostración local. La aplicación SOAP mantiene su propia configuración y contenedor.
