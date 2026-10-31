# Diseño: EurekaBank REST paralelo a SOAP

## Intención

Crear una copia funcional del proyecto EurekaBank que use una API REST en lugar de SOAP, guiándose por la arquitectura de `TG1.3 RESTFUL_JAVA_EUREKABANK_GR05 20.0` en Descargas. La nueva versión debe conservar la lógica y las interfaces del proyecto SOAP, y permitir ejecutar ambos proyectos en paralelo.

## Objetivos

- Crear el proyecto bajo `TG.1.3 PAO202651_REST_JAVA_MYSQL_NRC36883_GR09/`.
- Mantener el paquete Java `ec.edu.monster` en los módulos Java.
- Crear servidor REST y clientes web, escritorio, consola y Android.
- Conservar la apariencia, navegación y operaciones que ofrecen los clientes SOAP.
- Usar MySQL y el esquema/dataset EurekaBank del proyecto actual.
- Adoptar JAX-RS y JSON, siguiendo el servidor de la carpeta REST de referencia.
- Mantener intacto el proyecto SOAP actual.

## Fuera de alcance

- Reemplazar o renombrar los módulos SOAP.
- Cambiar la interfaz o añadir operaciones bancarias que no existan en SOAP.
- Migrar el motor de base de datos.

## Estructura acordada

```text
TG.1.3 PAO202651_REST_JAVA_MYSQL_NRC36883_GR09/
├── 03. DBB/mysql/init/
├── 04. SERVIDOR/WS_EurekaBank_RESTJAVA_GR09/
├── 04. CLIWEB/CLIWEB_EuBank_RESTJAVA_GR09/
├── 04. CLIESC/CLIESC_EuBank_RESTJAVA_GR09/
├── 04. CLICON/CLICON_EuBank_RESTJAVA_GR09/
├── 04. CLIMOV/CLIMOV_EurekaBank_RESTJAVA_GR09/
└── 05. DOCUMENTACION/
```

Todos los módulos Java conservarán `ec.edu.monster` como raíz de paquetes. El código Android mantendrá su estructura de paquetes existente bajo `ec.edu.monster`.

## Enfoque elegido y alternativas

1. **Proyecto paralelo completo (elegido):** crear los cinco módulos REST anteriores. El código de interfaz y la lógica de cliente parten del proyecto SOAP para preservar su comportamiento; los clientes cambian su adaptador de transporte para llamar a REST. El servidor sigue las convenciones JAX-RS/JSON de la referencia. Esto conserva todas las interfaces y deja SOAP ejecutable.
2. **Solo servidor y cliente web:** reduciría el trabajo, pero dejaría escritorio, consola y Android sin versión REST y no conservaría el conjunto de clientes que el usuario pidió.
3. **Copiar literalmente toda la carpeta de referencia:** se descartó porque sus pantallas y detalles pertenecen a otro proyecto/grupo; se reutilizan sus convenciones REST y arquitectura, pero la interfaz debe derivarse del SOAP actual.

## Arquitectura y correspondencia funcional

- **Servidor REST:** WAR Jakarta EE/JAX-RS con capas de recurso (`wsrest`), servicio, repositorio, modelos y acceso JDBC, siguiendo la estructura de referencia y conservando el dominio `ec.edu.monster`.
- **Contrato JSON bajo `/api/eurekabank`:** `POST /login`, `GET /cuentas`, `GET /movimientos?cuenta=...`, `POST /movimientos` para depósito/retiro/transferencia, y `GET /tipos`. Los cuerpos y respuestas mantendrán modelos equivalentes a `Usuario`, `DatosCuenta`, `MovimientoData` y `ResultadoOperacion`.
- **Lógica bancaria:** reutilizar consultas y reglas del SOAP, incluidas cuentas activas, movimientos ordenados, tipos permitidos, validación de importe, saldo suficiente y transacción atómica de transferencias.
- **Cliente web:** conservar controladores, plantillas, recursos visuales y navegación del SOAP; cambiar el servicio de integración para consumir endpoints REST.
- **Cliente escritorio y consola:** conservar vistas, flujo de login, consulta y operaciones; sustituir el cliente SOAP por llamadas HTTP/JSON.
- **Cliente Android:** conservar pantallas Compose, navegación, modelos visuales y operaciones; cambiar el servicio kSOAP por consumo HTTP/JSON conforme al patrón de la referencia.
- **Base de datos:** incluir los scripts de inicialización MySQL que utiliza el proyecto actual y documentar el arranque/configuración en el nuevo proyecto.
- **Configuración:** endpoints y parámetros de conexión se centralizarán mediante propiedades/configuración por entorno en vez de depender de una IP fija de la referencia.

## Consideraciones de seguridad y exactitud

La revisión del SOAP encontró endpoints sin autorización de sesión y uso de SHA-1/credenciales de demostración. Para mantener compatibilidad funcional, este trabajo conservará el mecanismo de login existente y no introducirá un sistema de tokens sin alcance aprobado. Evitará registrar contraseñas en logs y mantendrá las credenciales locales de demostración claramente separadas de la configuración de despliegue. La API REST no debe abrir consultas o movimientos a clientes sin una identidad validada si el diseño del proyecto puede sostener esa validación sin romper los flujos existentes; si la carpeta de referencia impone una limitación, se documentará.

## Verificación prevista

Revisar coherencia del contrato JSON con los cuatro clientes, configuración y estructura final; inspeccionar diferencias para confirmar que el SOAP original no se alteró. No se fija aquí una matriz de pruebas automatizadas; se decidirá durante el plan de implementación según la disponibilidad de dependencias y entorno.

## Decisiones pendientes del implementador

- Resolver detalles de build/servidor de aplicaciones respetando las versiones y convenciones del proyecto SOAP y de la referencia REST.
- Confirmar el contexto de despliegue REST y usarlo en propiedades documentadas para web, escritorio, consola y Android.
- Ajustar nombres de clases y rutas internas de cada cliente sin cambiar los nombres de carpetas acordados.
