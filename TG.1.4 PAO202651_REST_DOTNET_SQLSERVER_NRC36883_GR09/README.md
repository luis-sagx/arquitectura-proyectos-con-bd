# EurekaBank REST .NET + SQL Server

Port de la solución REST Java a .NET, guiado por la referencia RESTFUL_DOTNET_EUREKABANK_GR05. Conserva inicio de sesión, cuentas, movimientos, depósitos, retiros y transferencias. La API sirve `http://localhost:9090/api`; los clientes web, escritorio, consola y móvil mantienen esos flujos.

## Módulos

- `04. SERVIDOR/WS_EuBank_RESTDOTNET_GR09`: API ASP.NET Core 10 con Microsoft.Data.SqlClient.
- `04. CLIWEB/CLIWEB_EuBank_REST_DOTNET_GR09`: cliente web responsive.
- `04. CLIESC/CLIESC_EuBank_REST_DOTNET_GR09`: cliente Windows Forms.
- `04. CLICON/CLICON_EuBank_REST_DOTNET_GR09`: cliente interactivo de consola.
- `04. CLIMOV/CLIMOV_EurekaBank_REST_DOTNET_GR09`: cliente móvil .NET MAUI.
- `03. DBB/sqlserver/eurekabank.sql`: esquema y datos de ejemplo SQL Server, también compatible con el proyecto SOAP .NET.

## Arranque en Windows

### Usar SQL Server con Docker Compose

Desde la raíz del repositorio, crea el archivo local de variables. El usuario de la aplicación es `Monster` y su clave es `Monster9`:

```powershell
Copy-Item 'TG.1.4 PAO202651_REST_DOTNET_SQLSERVER_NRC36883_GR09/.env.example' 'TG.1.4 PAO202651_REST_DOTNET_SQLSERVER_NRC36883_GR09/.env'
notepad 'TG.1.4 PAO202651_REST_DOTNET_SQLSERVER_NRC36883_GR09/.env'
docker compose --env-file 'TG.1.4 PAO202651_REST_DOTNET_SQLSERVER_NRC36883_GR09/.env' -f 'TG.1.4 PAO202651_REST_DOTNET_SQLSERVER_NRC36883_GR09/docker-compose.sqlserver.yml' up -d
```

Compose espera a que SQL Server esté listo y luego ejecuta el script de esquema y datos de ejemplo. La base persiste en el volumen `eurekabank-rest-sqlserver-data`; el puerto del equipo anfitrión es `14332`.

Antes de ejecutar la API, configura la conexión desde la misma terminal:

```powershell
$env:DB_CONNECTION = "Server=localhost,14332;Database=EurekaBank;User ID=Monster;Password=Monster9;Encrypt=True;TrustServerCertificate=True;MultipleActiveResultSets=True"
```

Para detener el contenedor sin borrar la base: `docker compose --env-file 'TG.1.4 PAO202651_REST_DOTNET_SQLSERVER_NRC36883_GR09/.env' -f 'TG.1.4 PAO202651_REST_DOTNET_SQLSERVER_NRC36883_GR09/docker-compose.sqlserver.yml' down`.

Requisitos: SQL Server en ejecución, `sqlcmd` y .NET 10. El servidor local predeterminado se conecta a `localhost`, base `EurekaBank`, con autenticación integrada de Windows. Inicializa la base desde la raíz del repositorio:

```powershell
sqlcmd -S localhost -E -b -i "TG.1.4 PAO202651_REST_DOTNET_SQLSERVER_NRC36883_GR09/03. DBB/sqlserver/eurekabank.sql"
```

El script crea la base, tablas e índices, y carga los datos de ejemplo una sola vez. Para otra instancia o autenticación SQL, configura `DB_CONNECTION` antes de iniciar la API:

```powershell
$env:DB_CONNECTION = "Server=localhost;Database=EurekaBank;User ID=USUARIO;Password=CLAVE;Encrypt=True;TrustServerCertificate=True"
```

Inicia la API desde la raíz del repositorio:

```powershell
dotnet run --project 'TG.1.4 PAO202651_REST_DOTNET_SQLSERVER_NRC36883_GR09/04. SERVIDOR/WS_EuBank_RESTDOTNET_GR09/WS_EuBank_REST_DOTNET_GR09.csproj' --urls http://localhost:9090
```

En otra terminal inicia la web:

```powershell
dotnet run --project 'TG.1.4 PAO202651_REST_DOTNET_SQLSERVER_NRC36883_GR09/04. CLIWEB/CLIWEB_EuBank_REST_DOTNET_GR09/CLIWEB_EuBank_REST_DOTNET_GR09.csproj' --urls http://localhost:8091
```

Abre `http://localhost:8091`. Para escritorio usa `dotnet run --project 'TG.1.4 PAO202651_REST_DOTNET_SQLSERVER_NRC36883_GR09/04. CLIESC/CLIESC_EuBank_REST_DOTNET_GR09/CLIESC_EuBank_REST_DOTNET_GR09.csproj'`; la consola se inicia con el proyecto `04. CLICON`. MAUI requiere instalar el workload correspondiente (`dotnet workload install maui-android`). En Android Emulator se llega a la API del equipo anfitrión por `10.0.2.2:9090`.

Los endpoints principales son `POST /api/login`, `GET /api/cuentas`, `GET /api/cuentas/{codigo}`, `GET /api/movimientos?cuenta=...`, `GET /api/tiposmovimiento` y `POST /api/movimientos`.
