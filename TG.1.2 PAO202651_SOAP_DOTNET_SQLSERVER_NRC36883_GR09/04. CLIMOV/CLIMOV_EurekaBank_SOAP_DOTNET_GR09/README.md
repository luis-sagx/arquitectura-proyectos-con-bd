# Cliente móvil EurekaBank SOAP .NET

Proyecto .NET MAUI con acceso SOAP al servidor .NET. Incluye autenticación, selección de cuentas, consulta de movimientos y registro de depósitos, retiros y transferencias.

En Android Emulator usa `http://10.0.2.2:9091` para alcanzar el servidor que corre en el equipo anfitrión. En Windows/iOS usa `http://localhost:9091`. Para un teléfono físico, ajusta la URL de `SoapTransport.cs` a la IP LAN del equipo que ejecuta el servidor.

Requiere instalar el workload MAUI para la plataforma destino (`dotnet workload install maui-android` para Android).