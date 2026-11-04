# Cliente móvil EurekaBank REST .NET

Proyecto .NET MAUI con acceso a la API REST del servidor .NET. Incluye autenticación, selección de cuentas, consulta de movimientos y registro de depósitos, retiros y transferencias.

En Android Emulator usa `10.0.2.2:9090` para alcanzar el servidor que corre en el equipo anfitrión. En Windows/iOS usa `localhost:9090`. Para un teléfono físico, ajusta la URL de `BankApi.cs` a la IP LAN del equipo que ejecuta la API.

Requiere instalar el workload MAUI para la plataforma destino (`dotnet workload install maui-android` para Android). La máquina de desarrollo actual no tiene workloads MAUI instalados.
