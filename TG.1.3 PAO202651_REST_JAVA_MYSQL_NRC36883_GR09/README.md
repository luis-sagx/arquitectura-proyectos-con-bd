# EurekaBank REST Java + MySQL

Versión REST paralela al proyecto SOAP EurekaBank. Mantiene los cuatro clientes (web, escritorio Swing, consola y Android) y usa un servidor Jakarta REST con JSON.

## Módulos

- `04. SERVIDOR/WS_EurekaBank_RESTJAVA_GR09`: API Jakarta REST desplegable como WAR.
- `04. CLIWEB/CLIWEB_EuBank_RESTJAVA_GR09`: cliente Spring Boot y Thymeleaf, puerto `8081`.
- `04. CLIESC/CLIESC_EuBank_RESTJAVA_GR09`: cliente de escritorio Swing.
- `04. CLICON/CLICON_EuBank_RESTJAVA_GR09`: cliente de consola.
- `04. CLIMOV/CLIMOV_EurekaBank_RESTJAVA_GR09`: aplicación Android Compose.
- `03. DBB/mysql/init`: esquema y datos de demostración.
- `05. DOCUMENTACION/API-REST.md`: rutas y cuerpos JSON.

Todos los módulos Java conservan el paquete `ec.edu.monster`.

## Ejecución local

1. Copia `.env.example` a `.env` y configura la contraseña local de MySQL.
2. Para usar la instalación MySQL local creada, el servidor conecta por defecto a `localhost:3306`. Si prefieres Docker, inicia su MySQL en el puerto `3308`:

   ```powershell
   docker compose -f docker-compose.mysql.yml up -d
   ```

3. Despliega `WS_EurekaBank_RESTJAVA_GR09/target/WS_EurekaBank_RESTJAVA_GR09.war` en Payara u otro servidor Jakarta EE 10. El contexto esperado es `http://localhost:8080/WS_EurekaBank_RESTJAVA_GR09`.
4. Arranca el cliente web desde su carpeta:

   ```powershell
   mvn spring-boot:run
   ```

   Abre `http://localhost:8081`.
5. Ejecuta escritorio y consola desde sus respectivas carpetas con `mvn spring-boot:run`.
6. Para Android, abre el módulo en Android Studio y ejecuta `gradlew.bat assembleDebug`. El emulador usa por defecto `http://10.0.2.2:8080/WS_EurekaBank_RESTJAVA_GR09/api/eurekabank`; se puede sobrescribir con la propiedad Gradle `restApiUrl`.

La conexión JDBC por defecto del servidor apunta a `localhost:3306`, base `eurekabank`, usuario `monster`, contraseña local `monster9`. Se puede cambiar mediante `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER` y `DB_PASSWORD`. Si usas el MySQL de Docker en `3308`, establece `$env:DB_PORT = "3308"` en la terminal desde la que arrancas Payara. Los clientes Java permiten cambiar el endpoint con `EUBANK_REST_URL`.

Los scripts Compose inicializan el esquema y datos solo cuando el volumen está vacío. Para reiniciar la base desde cero, `docker compose -f docker-compose.mysql.yml down -v` elimina permanentemente los datos de ese volumen.

La base de datos y las credenciales incluidas son únicamente para desarrollo académico local.
