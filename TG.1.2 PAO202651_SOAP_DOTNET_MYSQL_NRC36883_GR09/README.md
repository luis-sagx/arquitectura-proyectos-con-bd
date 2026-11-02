# EurekaBank SOAP .NET

Servidor SOAP 1.1 y clientes .NET de consola, web, escritorio y móvil. Las interfaces y operaciones se conservan; la persistencia usa Microsoft SQL Server.

## Base de datos

Requisitos: SQL Server en ejecución, `sqlcmd` y .NET 10. El servidor local predeterminado se conecta a `localhost`, base `EurekaBank`, mediante autenticación integrada de Windows.

Desde la raíz del repositorio, ejecuta:

```powershell
sqlcmd -S localhost -E -b -i "TG.1.2 PAO202651_SOAP_DOTNET_MYSQL_NRC36883_GR09/03. DBB/sqlserver/eurekabank.sql"
```

El script crea la base si hace falta, define las tablas e índices, y carga las filas de ejemplo una sola vez. Si usas otra instancia o autenticación SQL, configura `DB_CONNECTION` antes de iniciar el servidor:

```powershell
$env:DB_CONNECTION = "Server=localhost;Database=EurekaBank;User ID=USUARIO;Password=CLAVE;Encrypt=True;TrustServerCertificate=True"
```

## Iniciar el servidor y los clientes

Servidor SOAP (escucha en `http://localhost:9091`):

```powershell
dotnet run --project "04. SERVIDOR/WS_EuBank_SOAPDOTNET_GR09/WS_EuBank_SOAPDOTNET_GR09.csproj"
```

- WSDL de autenticación: `http://localhost:9091/Services/LoginService.svc?wsdl`
- WSDL de cuentas y movimientos: `http://localhost:9091/Services/MovimientosService.svc?wsdl`

En terminales separadas:

```powershell
dotnet run --project "04. CLICON/CLICON_EuBank_SOAP_DOTNET_GR09/CLICON_EuBank_SOAP_DOTNET_GR09.csproj"
dotnet run --project "04. CLIWEB/CLIWEB_EuBank_SOAP_DOTNET_GR09/CLIWEB_EuBank_SOAP_DOTNET_GR09.csproj"
dotnet run --project "04. CLIESC/CLIESC_EuBank_SOAP_DOTNET_GR09/CLIESC_EuBank_SOAP_DOTNET_GR09.csproj"
```

La web abre en `http://localhost:8092`. El cliente móvil requiere el workload MAUI; en Android Emulator, el servidor anfitrión se alcanza por `10.0.2.2:9091`.

Los clientes permiten iniciar sesión, consultar cuentas y movimientos, y registrar depósitos, retiros y transferencias. Las transferencias se confirman dentro de una transacción SQL Server.