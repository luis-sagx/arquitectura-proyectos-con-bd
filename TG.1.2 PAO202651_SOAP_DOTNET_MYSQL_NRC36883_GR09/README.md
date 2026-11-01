# EuBank SOAP .NET · GR09

Proyecto .NET SOAP que conserva el flujo y las interfaces de los clientes .NET REST del repositorio. El servidor ofrece SOAP 1.1 sobre HTTP y guarda las operaciones en MySQL.

## Estructura

- `04. SERVIDOR/WS_EuBank_SOAPDOTNET_GR09`: endpoints `LoginService.svc` y `MovimientosService.svc`, contratos SOAP y acceso MySQL.
- `04. CLICON/CLICON_EuBank_SOAP_DOTNET_GR09`: cliente de consola.
- `04. CLIWEB/CLIWEB_EuBank_SOAP_DOTNET_GR09`: cliente web.
- `04. CLIESC/CLIESC_EuBank_SOAP_DOTNET_GR09`: cliente de escritorio.
- `04. CLIMOV/CLIMOV_EurekaBank_SOAP_DOTNET_GR09`: cliente móvil .NET MAUI.
- `03. DBB/mysql`: esquema y datos de EurekaBank.

## Conexión a MySQL

El servidor SOAP usa MySQL local en `localhost:3306`, base `eurekabank` y usuario `root`. Define `DB_CONNECTION` con las credenciales configuradas en tu equipo antes de iniciarlo.

La alternativa Docker crea una base aislada en el puerto `3309`. Desde esta carpeta, ejecuta:

```powershell
docker compose -f docker-compose.mysql.yml up -d
```

El contenedor usa la base `eurekabank` y el usuario `monster` con clave `monster9`. Para que el servidor use ese contenedor, configura:

```powershell
$env:DB_CONNECTION = "Server=localhost;Port=3309;Database=eurekabank;User ID=monster;Password=monster9;SslMode=None;Allow User Variables=True"
```

Si tu MySQL usa otro puerto o usuario, configura `DB_CONNECTION` antes de iniciar el servidor. Ejemplo:

```powershell
$env:DB_CONNECTION = "Server=localhost;Port=3306;Database=eurekabank;User ID=root;Password=TU_CLAVE;SslMode=None;Allow User Variables=True"
```

## Iniciar el servidor SOAP

```powershell
dotnet run --project "04. SERVIDOR/WS_EuBank_SOAPDOTNET_GR09/WS_EuBank_SOAPDOTNET_GR09.csproj"
```

El servicio escucha en `http://localhost:9091`.

- WSDL de autenticación: `http://localhost:9091/Services/LoginService.svc?wsdl`
- WSDL de cuentas y movimientos: `http://localhost:9091/Services/MovimientosService.svc?wsdl`

## Iniciar los clientes

En terminales separadas, con el servidor en ejecución:

```powershell
dotnet run --project "04. CLICON/CLICON_EuBank_SOAP_DOTNET_GR09/CLICON_EuBank_SOAP_DOTNET_GR09.csproj"
dotnet run --project "04. CLIWEB/CLIWEB_EuBank_SOAP_DOTNET_GR09/CLIWEB_EuBank_SOAP_DOTNET_GR09.csproj"
dotnet run --project "04. CLIESC/CLIESC_EuBank_SOAP_DOTNET_GR09/CLIESC_EuBank_SOAP_DOTNET_GR09.csproj"
```

La web abre en `http://localhost:8092`. El cliente móvil requiere instalar el workload MAUI para su plataforma. Para Android Emulator:

```powershell
dotnet workload install maui-android
dotnet run --project "04. CLIMOV/CLIMOV_EurekaBank_SOAP_DOTNET_GR09/CLIMOV_EurekaBank_SOAP_DOTNET_GR09.csproj" --framework net10.0-android
```

En Android, el cliente usa `10.0.2.2` para alcanzar el servidor SOAP de la máquina anfitriona.

## Funciones

Los cuatro clientes incluyen inicio de sesión, consulta de cuentas, movimientos globales o por cuenta y creación de depósitos, retiros y transferencias. Las listas muestran primero los movimientos más recientes. Las transferencias registran los movimientos de salida y entrada dentro de una transacción MySQL.
