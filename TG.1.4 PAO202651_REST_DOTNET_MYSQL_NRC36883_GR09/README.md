# EurekaBank REST .NET + MySQL

Port de la solución REST Java a .NET, guiado por la referencia RESTFUL_DOTNET_EUREKABANK_GR05. Conserva las funciones de inicio de sesión, cuentas, movimientos, depósitos, retiros y transferencias. La API sirve `http://localhost:9090/api` y los clientes web y escritorio mantienen sus interfaces; el cliente móvil ofrece esos mismos flujos en MAUI.

## Módulos

- `04. SERVIDOR/WS_EuBank_RESTDOTNET_GR09`: API ASP.NET Core 10, MySqlConnector y acceso MySQL.
- `04. CLIWEB/CLIWEB_EuBank_REST_DOTNET_GR09`: cliente web responsive.
- `04. CLIESC/CLIESC_EuBank_REST_DOTNET_GR09`: cliente Windows Forms; requiere Windows.
- `04. CLICON/CLICON_EuBank_REST_DOTNET_GR09`: cliente interactivo de consola.
- `04. CLIMOV/CLIMOV_EurekaBank_REST_DOTNET_GR09`: cliente móvil .NET MAUI para Android/iOS y Windows.
- `03. DBB/mysql`: esquema, datos, migración de orden global de movimientos y Docker Compose opcional.

## Arranque en Windows

1. Inicia el servicio MySQL `MySQL84` en el puerto `3306`. La conexión local predeterminada está en el servidor; reemplázala con la variable `DB_CONNECTION` si tu usuario/clave difieren.
2. Inicia la API desde la raíz del repositorio:

   ```powershell
   dotnet run --project 'TG.1.4 PAO202651_REST_DOTNET_MYSQL_NRC36883_GR09/04. SERVIDOR/WS_EuBank_RESTDOTNET_GR09/WS_EuBank_REST_DOTNET_GR09.csproj' --urls http://localhost:9090
   ```

3. Inicia la web en otra terminal:

   ```powershell
   dotnet run --project 'TG.1.4 PAO202651_REST_DOTNET_MYSQL_NRC36883_GR09/04. CLIWEB/CLIWEB_EuBank_REST_DOTNET_GR09/CLIWEB_EuBank_REST_DOTNET_GR09.csproj' --urls http://localhost:8091
   ```

4. Abre `http://localhost:8091`. Para escritorio, ejecuta `dotnet run --project 'TG.1.4 PAO202651_REST_DOTNET_MYSQL_NRC36883_GR09/04. CLIESC/CLIESC_EuBank_REST_DOTNET_GR09/CLIESC_EuBank_REST_DOTNET_GR09.csproj'` o abre `EurekaBank_REST_DOTNET_GR09.slnx` en Visual Studio.

El cliente de consola se inicia con `dotnet run --project 'TG.1.4 PAO202651_REST_DOTNET_MYSQL_NRC36883_GR09/04. CLICON/CLICON_EuBank_REST_DOTNET_GR09/CLICON_EuBank_REST_DOTNET_GR09.csproj'`.

### Alternativa con MySQL en Docker

Desde la raíz del repositorio copia `.env.example` a `.env`, cambia `MYSQL_ROOT_PASSWORD` y ejecuta `docker compose -f 'TG.1.4 PAO202651_REST_DOTNET_MYSQL_NRC36883_GR09/docker-compose.mysql.yml' --env-file 'TG.1.4 PAO202651_REST_DOTNET_MYSQL_NRC36883_GR09/.env' up -d`. El contenedor publica MySQL en el puerto `3308`; antes de iniciar la API configura:

```powershell
$env:DB_CONNECTION='Server=localhost;Port=3308;Database=eurekabank;User ID=monster;Password=monster9;SslMode=None;Allow User Variables=True'
```

La solución `EurekaBank_REST_DOTNET_GR09.slnx` compila API, web, consola y escritorio. MAUI está en su propio proyecto porque requiere instalar el workload correspondiente (`dotnet workload install maui-android` para Android). En Android Emulator la API se alcanza por `10.0.2.2:9090`; para un teléfono físico configura en `CLIMOV/.../BankApi.cs` la IP local del equipo servidor.

## Endpoints REST

`POST /api/login`, `GET /api/cuentas`, `GET /api/cuentas/{codigo}`, `GET /api/movimientos?cuenta=...`, `GET /api/tiposmovimiento` y `POST /api/movimientos`.
