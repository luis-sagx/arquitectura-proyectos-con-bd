-- EurekaBank schema for Microsoft SQL Server.
IF DB_ID(N'EurekaBank') IS NULL
    EXEC(N'CREATE DATABASE [EurekaBank]');
GO
USE [EurekaBank];
GO
SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO
-- Adapted from the EurekaBank schema; adapted for SQL Server.
-- The script creates EurekaBank and loads its sample data.


-- =============================================
-- Ultimos cambios
-- =============================================



-- =============================================
-- Creación de la Base de Datos
-- =============================================




-- =============================================
-- Seleccionar la Base de Datos
-- =============================================

-- =============================================
-- Creación de los Objetos de la Base de Datos
-- =============================================


IF OBJECT_ID(N'dbo.TipoMovimiento', N'U') IS NULL
BEGIN
CREATE TABLE dbo.TipoMovimiento (
	chr_tipocodigo       CHAR(3) NOT NULL,
	vch_tipodescripcion  VARCHAR(40) NOT NULL,
	vch_tipoaccion       VARCHAR(10) NOT NULL,
	vch_tipoestado       VARCHAR(15) DEFAULT 'ACTIVO' NOT NULL,
	CONSTRAINT PK_TipoMovimiento
		PRIMARY KEY (chr_tipocodigo),
	CONSTRAINT chk_tipomovimiento_vch_tipoaccion
		CHECK (vch_tipoaccion IN ('INGRESO', 'SALIDA')),
	CONSTRAINT chk_tipomovimiento_vch_tipoestado
		CHECK (vch_tipoestado IN ('ACTIVO', 'ANULADO', 'CANCELADO'))
);
END;
GO


IF OBJECT_ID(N'dbo.Sucursal', N'U') IS NULL
BEGIN
CREATE TABLE dbo.Sucursal (
	chr_sucucodigo       CHAR(3) NOT NULL,
	vch_sucunombre       VARCHAR(50) NOT NULL,
	vch_sucuciudad       VARCHAR(30) NOT NULL,
	vch_sucudireccion    VARCHAR(50) NULL,
		int_sucucontcuenta   INTEGER NOT NULL,
	CONSTRAINT PK_Sucursal
		PRIMARY KEY (chr_sucucodigo)
);
END;
GO


IF OBJECT_ID(N'dbo.Empleado', N'U') IS NULL
BEGIN
CREATE TABLE dbo.Empleado (
	chr_emplcodigo       CHAR(4) NOT NULL,
	vch_emplpaterno      VARCHAR(25) NOT NULL,
	vch_emplmaterno      VARCHAR(25) NOT NULL,
	vch_emplnombre       VARCHAR(30) NOT NULL,
	vch_emplciudad       VARCHAR(30) NOT NULL,
	vch_empldireccion    VARCHAR(50) NULL,
	CONSTRAINT PK_Empleado PRIMARY KEY (chr_emplcodigo)
);
END;
GO


IF OBJECT_ID(N'dbo.Modulo', N'U') IS NULL
BEGIN
CREATE TABLE dbo.Modulo
(
	int_moducodigo       INTEGER NOT NULL,
	vch_modunombre       VARCHAR(50) NULL,
	vch_moduestado       VARCHAR(15) NOT NULL
						           DEFAULT 'ACTIVO'
		                   CHECK ( vch_moduestado IN ('ACTIVO', 'ANULADO', 'CANCELADO') ),
	CONSTRAINT PK_Modulo PRIMARY KEY (int_moducodigo)
);
END;
GO


IF OBJECT_ID(N'dbo.Usuario', N'U') IS NULL
BEGIN
CREATE TABLE dbo.Usuario
(
	chr_emplcodigo       CHAR(4) NOT NULL,
	vch_emplusuario      VARCHAR(20) NOT NULL,
	vch_emplclave        VARCHAR(50) NOT NULL,
  vch_emplestado       VARCHAR(15) NULL DEFAULT 'ACTIVO'
                       CHECK ( vch_emplestado IN ('ACTIVO', 'ANULADO', 'CANCELADO') ),
	CONSTRAINT PK_Usuario PRIMARY KEY (chr_emplcodigo),
	CONSTRAINT U_Usuario_vch_emplusuario UNIQUE (vch_emplusuario),
	CONSTRAINT [FK_Usuario_Empleado] FOREIGN KEY (chr_emplcodigo) REFERENCES dbo.[Empleado] (chr_emplcodigo)
);
END;
GO


IF OBJECT_ID(N'dbo.Permiso', N'U') IS NULL
BEGIN
CREATE TABLE dbo.Permiso
(
	chr_emplcodigo       CHAR(4) NOT NULL,
	int_moducodigo       INTEGER NOT NULL,
	vch_permestado       VARCHAR(15) NOT NULL
	                     DEFAULT 'ACTIVO'
						           CHECK ( vch_permestado IN ('ACTIVO', 'ANULADO', 'CANCELADO') ),
	CONSTRAINT PK_Permiso PRIMARY KEY (chr_emplcodigo,int_moducodigo),
	CONSTRAINT [FK_Permiso_Modulo] FOREIGN KEY (int_moducodigo) REFERENCES dbo.[Modulo] (int_moducodigo),
	CONSTRAINT [FK_Permiso_Usuario] FOREIGN KEY (chr_emplcodigo) REFERENCES dbo.[Usuario] (chr_emplcodigo)
);
END;
GO


IF OBJECT_ID(N'dbo.LOGSESSION', N'U') IS NULL
BEGIN
CREATE TABLE dbo.LOGSESSION (
	ID                 INT IDENTITY(1,1) NOT NULL,
	chr_emplcodigo     CHAR(4) NOT NULL,
	fec_ingreso        DATETIME NOT NULL,
	fec_salida         DATETIME NULL,
	ip                 VARCHAR(20) NOT NULL DEFAULT 'NONE',
  hostname           VARCHAR(100) NOT NULL DEFAULT 'NONE',
	CONSTRAINT PK_LOG_SESSION PRIMARY KEY (ID),
	CONSTRAINT fk_log_session_empleado
		FOREIGN KEY (chr_emplcodigo)
		REFERENCES dbo.[Empleado] (chr_emplcodigo)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);
END;
GO


IF OBJECT_ID(N'dbo.Asignado', N'U') IS NULL
BEGIN
CREATE TABLE dbo.Asignado (
	chr_asigcodigo       CHAR(6) NOT NULL,
	chr_sucucodigo       CHAR(3) NOT NULL,
	chr_emplcodigo       CHAR(4) NOT NULL,
	dtt_asigfechaalta    DATE NOT NULL,
	dtt_asigfechabaja    DATE NULL,
	CONSTRAINT PK_Asignado
		PRIMARY KEY (chr_asigcodigo),
	CONSTRAINT fk_asignado_empleado
		FOREIGN KEY (chr_emplcodigo)
		REFERENCES dbo.[Empleado] (chr_emplcodigo)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT fk_asignado_sucursal
		FOREIGN KEY (chr_sucucodigo)
		REFERENCES dbo.[Sucursal] (chr_sucucodigo)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);
END;
GO

IF OBJECT_ID(N'dbo.Cliente', N'U') IS NULL
BEGIN
CREATE TABLE dbo.Cliente (
	chr_cliecodigo       CHAR(5) NOT NULL,
	vch_cliepaterno      VARCHAR(25) NOT NULL,
	vch_cliematerno      VARCHAR(25) NOT NULL,
	vch_clienombre       VARCHAR(30) NOT NULL,
	chr_cliedni          CHAR(8) NOT NULL,
	vch_clieciudad       VARCHAR(30) NOT NULL,
	vch_cliedireccion    VARCHAR(50) NOT NULL,
	vch_clietelefono     VARCHAR(20) NULL,
	vch_clieemail        VARCHAR(50) NULL,
	CONSTRAINT PK_Cliente
		PRIMARY KEY (chr_cliecodigo)
);
END;
GO

IF OBJECT_ID(N'dbo.Moneda', N'U') IS NULL
BEGIN
CREATE TABLE dbo.Moneda (
	chr_monecodigo       CHAR(2) NOT NULL,
	vch_monedescripcion  VARCHAR(20) NOT NULL,
	CONSTRAINT PK_Moneda
		PRIMARY KEY (chr_monecodigo)
);
END;
GO

IF OBJECT_ID(N'dbo.Cuenta', N'U') IS NULL
BEGIN
CREATE TABLE dbo.Cuenta (
	chr_cuencodigo       CHAR(8) NOT NULL,
	chr_monecodigo       CHAR(2) NOT NULL,
	chr_sucucodigo       CHAR(3) NOT NULL,
	chr_emplcreacuenta   CHAR(4) NOT NULL,
	chr_cliecodigo       CHAR(5) NOT NULL,
	dec_cuensaldo        DECIMAL(12,2) NOT NULL,
	dtt_cuenfechacreacion DATE NOT NULL,
	vch_cuenestado       VARCHAR(15) DEFAULT 'ACTIVO' NOT NULL,
	int_cuencontmov      INTEGER NOT NULL,
	chr_cuenclave        CHAR(6) NOT NULL,
	CONSTRAINT chk_cuenta_chr_cuenestado
		CHECK (vch_cuenestado IN ('ACTIVO', 'ANULADO', 'CANCELADO')),
	CONSTRAINT PK_Cuenta
		PRIMARY KEY (chr_cuencodigo),
	CONSTRAINT fk_cuenta_cliente
		FOREIGN KEY (chr_cliecodigo)
		REFERENCES dbo.[Cliente] (chr_cliecodigo)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT fk_cuente_empleado
		FOREIGN KEY (chr_emplcreacuenta)
		REFERENCES dbo.[Empleado] (chr_emplcodigo)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT fk_cuenta_sucursal
		FOREIGN KEY (chr_sucucodigo)
		REFERENCES dbo.[Sucursal] (chr_sucucodigo)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT fk_cuenta_moneda
		FOREIGN KEY (chr_monecodigo)
		REFERENCES dbo.[Moneda] (chr_monecodigo)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);
END;
GO

IF OBJECT_ID(N'dbo.Movimiento', N'U') IS NULL
BEGIN
CREATE TABLE dbo.Movimiento (
	id_movimiento        BIGINT IDENTITY(1,1) NOT NULL,
	chr_cuencodigo       CHAR(8) NOT NULL,
	int_movinumero       INTEGER NOT NULL,
	dtt_movifecha        DATE NOT NULL,
	chr_emplcodigo       CHAR(4) NOT NULL,
	chr_tipocodigo       CHAR(3) NOT NULL,
	dec_moviimporte      DECIMAL(12,2) NOT NULL,
	chr_cuenreferencia   CHAR(8) NULL,
	CONSTRAINT chk_Movimiento_importe4
		CHECK (dec_moviimporte >= 0.0),
	CONSTRAINT PK_Movimiento
		PRIMARY KEY (chr_cuencodigo, int_movinumero),
    CONSTRAINT [uq_movimiento_id] UNIQUE (id_movimiento),
	CONSTRAINT fk_movimiento_tipomovimiento
		FOREIGN KEY (chr_tipocodigo)
		REFERENCES dbo.[TipoMovimiento] (chr_tipocodigo)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT fk_movimiento_empleado
		FOREIGN KEY (chr_emplcodigo)
		REFERENCES dbo.[Empleado] (chr_emplcodigo)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION,
	CONSTRAINT fk_movimiento_cuenta
		FOREIGN KEY (chr_cuencodigo)
		REFERENCES dbo.[Cuenta] (chr_cuencodigo)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);
END;
GO

IF OBJECT_ID(N'dbo.Parametro', N'U') IS NULL
BEGIN
CREATE TABLE dbo.Parametro (
	chr_paracodigo       CHAR(3) NOT NULL,
	vch_paradescripcion  VARCHAR(50) NOT NULL,
	vch_paravalor        VARCHAR(70) NOT NULL,
	vch_paraestado       VARCHAR(15) DEFAULT 'ACTIVO' NOT NULL,
	CONSTRAINT chk_parametro_vch_paraestado
		CHECK (vch_paraestado IN ('ACTIVO', 'ANULADO', 'CANCELADO')),
	CONSTRAINT PK_Parametro
		PRIMARY KEY (chr_paracodigo)
);
END;
GO

IF OBJECT_ID(N'dbo.InteresMensual', N'U') IS NULL
BEGIN
CREATE TABLE dbo.InteresMensual (
	chr_monecodigo       CHAR(2) NOT NULL,
	dec_inteimporte      DECIMAL(12,2) NOT NULL,
	CONSTRAINT PK_InteresMensual
		PRIMARY KEY (chr_monecodigo),
	CONSTRAINT fk_interesmensual_moneda
		FOREIGN KEY (chr_monecodigo)
		REFERENCES dbo.[Moneda] (chr_monecodigo)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);
END;
GO

IF OBJECT_ID(N'dbo.CostoMovimiento', N'U') IS NULL
BEGIN
CREATE TABLE dbo.CostoMovimiento (
	chr_monecodigo       CHAR(2) NOT NULL,
	dec_costimporte      DECIMAL(12,2) NOT NULL,
	CONSTRAINT PK_CostoMovimiento
		PRIMARY KEY (chr_monecodigo),
	CONSTRAINT fk_costomovimiento_moneda
		FOREIGN KEY (chr_monecodigo)
		REFERENCES dbo.[Moneda] (chr_monecodigo)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);
END;
GO

IF OBJECT_ID(N'dbo.CargoMantenimiento', N'U') IS NULL
BEGIN
CREATE TABLE dbo.CargoMantenimiento (
	chr_monecodigo       CHAR(2) NOT NULL,
	dec_cargMontoMaximo  DECIMAL(12,2) NOT NULL,
	dec_cargImporte      DECIMAL(12,2) NOT NULL,
	CONSTRAINT PK_CargoMantenimiento
		PRIMARY KEY (chr_monecodigo),
	CONSTRAINT fk_cargomantenimiento_moneda
		FOREIGN KEY (chr_monecodigo)
		REFERENCES dbo.[Moneda] (chr_monecodigo)
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
);
END;
GO

IF OBJECT_ID(N'dbo.Contador', N'U') IS NULL
BEGIN
CREATE TABLE dbo.Contador (
	vch_conttabla        VARCHAR(30) NOT NULL,
	int_contitem         INTEGER NOT NULL,
	int_contlongitud     INTEGER NOT NULL,
	CONSTRAINT PK_Contador
		PRIMARY KEY (vch_conttabla)
);
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'idx_asignado01' AND object_id = OBJECT_ID(N'dbo.Asignado'))
    CREATE INDEX [idx_asignado01] ON dbo.[Asignado] (chr_emplcodigo);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'idx_asignado02' AND object_id = OBJECT_ID(N'dbo.Asignado'))
    CREATE INDEX [idx_asignado02] ON dbo.[Asignado] (chr_sucucodigo);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'idx_cuenta01' AND object_id = OBJECT_ID(N'dbo.Cuenta'))
    CREATE INDEX [idx_cuenta01] ON dbo.[Cuenta] (chr_cliecodigo);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'idx_cuenta02' AND object_id = OBJECT_ID(N'dbo.Cuenta'))
    CREATE INDEX [idx_cuenta02] ON dbo.[Cuenta] (chr_emplcreacuenta);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'idx_cuenta03' AND object_id = OBJECT_ID(N'dbo.Cuenta'))
    CREATE INDEX [idx_cuenta03] ON dbo.[Cuenta] (chr_sucucodigo);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'idx_cuenta04' AND object_id = OBJECT_ID(N'dbo.Cuenta'))
    CREATE INDEX [idx_cuenta04] ON dbo.[Cuenta] (chr_monecodigo);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'idx_movimiento01' AND object_id = OBJECT_ID(N'dbo.Movimiento'))
    CREATE INDEX [idx_movimiento01] ON dbo.[Movimiento] (chr_tipocodigo);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'idx_movimiento02' AND object_id = OBJECT_ID(N'dbo.Movimiento'))
    CREATE INDEX [idx_movimiento02] ON dbo.[Movimiento] (chr_emplcodigo);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'idx_movimiento03' AND object_id = OBJECT_ID(N'dbo.Movimiento'))
    CREATE INDEX [idx_movimiento03] ON dbo.[Movimiento] (chr_cuencodigo);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'idx_interesmensual01' AND object_id = OBJECT_ID(N'dbo.InteresMensual'))
    CREATE INDEX [idx_interesmensual01] ON dbo.[InteresMensual] (chr_monecodigo);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'idx_costomovimiento' AND object_id = OBJECT_ID(N'dbo.CostoMovimiento'))
    CREATE INDEX [idx_costomovimiento] ON dbo.[CostoMovimiento] (chr_monecodigo);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'idx_cargomantenimiento01' AND object_id = OBJECT_ID(N'dbo.CargoMantenimiento'))
    CREATE INDEX [idx_cargomantenimiento01] ON dbo.[CargoMantenimiento] (chr_monecodigo);
GO

-- Sample rows from the EurekaBank demonstration dataset; load only an empty database.
SET XACT_ABORT ON;
BEGIN TRANSACTION;
IF NOT EXISTS (SELECT 1 FROM dbo.TipoMovimiento)
BEGIN
-- Sample data adapted from the public EurekaBank demonstration dataset.
-- Loaded once after the schema is created in an empty SQL Server database.
/*
Empresa        :  EurekaBank
Software       :  Sistema de Cuentas de Ahorro
DBMS           :  Microsoft SQL Server
Base de Datos  :  eurekabank
Script         :  Carga Datos
Responsable    :  Ing. Eric Gustavo Coronel Castillo
Email          :  gcoronelc@gmail.com
Sitio Web      :  www.desarrollasoftware.com
Blog           :  http://gcoronelc.blogspot.com
*/

-- =============================================
-- Seleccionar la base de datos
-- =============================================

-- =============================================
-- Cargar Datos de Prueba
-- =============================================

-- Tabla: Moneda
INSERT INTO dbo.Moneda values ( '01', 'Soles' );
INSERT INTO dbo.Moneda values ( '02', 'Dolares' );

-- Tabla: CargoMantenimiento
INSERT INTO dbo.CargoMantenimiento values ( '01', 3500.00, 7.00 );
INSERT INTO dbo.CargoMantenimiento values ( '02', 1200.00, 2.50 );

-- Tabla: CargoMovimiento
INSERT INTO dbo.CostoMovimiento values ( '01', 2.00 );
INSERT INTO dbo.CostoMovimiento values ( '02', 0.60 );

-- Tabla: InteresMensual
INSERT INTO dbo.InteresMensual values ( '01', 0.70 );
INSERT INTO dbo.InteresMensual values ( '02', 0.60 );

-- Tabla: TipoMovimiento
INSERT INTO dbo.TipoMovimiento values( '001', 'Apertura de Cuenta', 'INGRESO', 'ACTIVO' );
INSERT INTO dbo.TipoMovimiento values( '002', 'Cancelar Cuenta', 'SALIDA', 'ACTIVO' );
INSERT INTO dbo.TipoMovimiento values( '003', 'Deposito', 'INGRESO', 'ACTIVO' );
INSERT INTO dbo.TipoMovimiento values( '004', 'Retiro', 'SALIDA', 'ACTIVO' );
INSERT INTO dbo.TipoMovimiento values( '005', 'Interes', 'INGRESO', 'ACTIVO' );
INSERT INTO dbo.TipoMovimiento values( '006', 'Mantenimiento', 'SALIDA', 'ACTIVO' );
INSERT INTO dbo.TipoMovimiento values( '007', 'ITF', 'SALIDA', 'ACTIVO' );
INSERT INTO dbo.TipoMovimiento values( '008', 'Transferencia', 'INGRESO', 'ACTIVO' );
INSERT INTO dbo.TipoMovimiento values( '009', 'Transferencia', 'SALIDA', 'ACTIVO' );
INSERT INTO dbo.TipoMovimiento values( '010', 'Cargo por Movimiento', 'SALIDA', 'ACTIVO' );

-- Tabla: Sucursal
INSERT INTO dbo.Sucursal values( '001', 'Sipan', 'Chiclayo', 'Av. Balta 1456', 2 );
INSERT INTO dbo.Sucursal values( '002', 'Chan Chan', 'Trujillo', 'Jr. Independencia 456', 3 );
INSERT INTO dbo.Sucursal values( '003', 'Los Olivos', 'Lima', 'Av. Central 1234', 0 );
INSERT INTO dbo.Sucursal values( '004', 'Pardo', 'Lima', 'Av. Pardo 345 - Miraflores', 0 );
INSERT INTO dbo.Sucursal values( '005', 'Misti', 'Arequipa', 'Bolivar 546', 0 );
INSERT INTO dbo.Sucursal values( '006', 'Machupicchu', 'Cusco', 'Calle El Sol 534', 0 );
INSERT INTO dbo.Sucursal values( '007', 'Grau', 'Piura', 'Av. Grau 1528', 0 );


-- Tabla: Empleado
INSERT INTO dbo.Empleado VALUES( '9999', 'Internet', 'Internet', 'internet', 'Internet', 'internet' );
INSERT INTO dbo.Empleado VALUES( '0001', 'Romero', 'Castillo', 'Carlos Alberto', 'Trujillo', 'Call1 1 Nro. 456' );
INSERT INTO dbo.Empleado VALUES( '0002', 'Castro', 'Vargas', 'Lidia', 'Lima', 'Federico Villarreal 456 - SMP' );
INSERT INTO dbo.Empleado VALUES( '0003', 'Reyes', 'Ortiz', 'Claudia', 'Lima', 'Av. Aviación 3456 - San Borja' );
INSERT INTO dbo.Empleado VALUES( '0004', 'Ramos', 'Garibay', 'Angelica', 'Chiclayo', 'Calle Barcelona 345' );
INSERT INTO dbo.Empleado VALUES( '0005', 'Ruiz', 'Zabaleta', 'Claudia', 'Cusco', 'Calle Cruz Verde 364' );
INSERT INTO dbo.Empleado VALUES( '0006', 'Cruz', 'Tarazona', 'Ricardo', 'Areguipa', 'Calle La Gruta 304' );
INSERT INTO dbo.Empleado VALUES( '0007', 'Diaz', 'Flores', 'Edith', 'Lima', 'Av. Pardo 546' );
INSERT INTO dbo.Empleado VALUES( '0008', 'Sarmiento', 'Bellido', 'Claudia Rocio', 'Areguipa', 'Calle Alfonso Ugarte 1567' );
INSERT INTO dbo.Empleado VALUES( '0009', 'Pachas', 'Sifuentes', 'Luis Alberto', 'Trujillo', 'Francisco Pizarro 1263' );
INSERT INTO dbo.Empleado VALUES( '0010', 'Tello', 'Alarcon', 'Hugo Valentin', 'Cusco', 'Los Angeles 865' );
INSERT INTO dbo.Empleado VALUES( '0011', 'Carrasco', 'Vargas', 'Pedro Hugo', 'Chiclayo', 'Av. Balta 1265' );
INSERT INTO dbo.Empleado VALUES( '0012', 'Mendoza', 'Jara', 'Monica Valeria', 'Lima', 'Calle Las Toronjas 450' );
INSERT INTO dbo.Empleado VALUES( '0013', 'Espinoza', 'Melgar', 'Victor Eduardo', 'Huancayo', 'Av. San Martin 6734 Dpto. 508 ' );
INSERT INTO dbo.Empleado VALUES( '0014', 'Hidalgo', 'Sandoval', 'Milagros Leonor', 'Chiclayo', 'Av. Luis Gonzales 1230' );


-- Tabla: Usuario
INSERT INTO dbo.Usuario VALUES( '0001',  'Monster',      LOWER(CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'Monster9'), 2)), 'ACTIVO' );
-- Las cuentas siguientes se conservan anuladas porque Permiso referencia sus empleados.
-- Solo Monster queda habilitada para iniciar sesión.
INSERT INTO dbo.Usuario VALUES( '0002', 'lcastro', LOWER(CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'flaca'), 2)), 'ANULADO' );
INSERT INTO dbo.Usuario VALUES( '0003', 'creyes', LOWER(CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'linda'), 2)), 'ANULADO' );
INSERT INTO dbo.Usuario VALUES( '0004', 'aramos', LOWER(CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'china'), 2)), 'ANULADO' );
INSERT INTO dbo.Usuario VALUES( '0005', 'cvalencia', LOWER(CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'angel'), 2)), 'ANULADO' );
INSERT INTO dbo.Usuario VALUES( '0006', 'rcruz', LOWER(CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'cerebro'), 2)), 'ANULADO' );
INSERT INTO dbo.Usuario VALUES( '0007', 'ediaz', LOWER(CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'princesa'), 2)), 'ANULADO' );
INSERT INTO dbo.Usuario VALUES( '0008', 'csarmiento', LOWER(CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'chinita'), 2)), 'ANULADO' );
INSERT INTO dbo.Usuario VALUES( '0009', 'lpachas', LOWER(CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'gato'), 2)), 'ANULADO' );
INSERT INTO dbo.Usuario VALUES( '0010', 'htello', LOWER(CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'machupichu'), 2)), 'ANULADO' );
INSERT INTO dbo.Usuario VALUES( '0011', 'pcarrasco', LOWER(CONVERT(VARCHAR(40), HASHBYTES('SHA1', 'tinajones'), 2)), 'ANULADO' );

-- Tabla: Modulo
INSERT INTO dbo.Modulo VALUES( 1, 'Procesos', 'ACTIVO');
INSERT INTO dbo.Modulo VALUES( 2, 'Tablas', 'ACTIVO');
INSERT INTO dbo.Modulo VALUES( 3, 'Consultas', 'ACTIVO');
INSERT INTO dbo.Modulo VALUES( 4, 'Reportes', 'ACTIVO');
INSERT INTO dbo.Modulo VALUES( 5, 'Util', 'ACTIVO');
INSERT INTO dbo.Modulo VALUES( 6, 'Seguridad', 'ACTIVO');

-- Tabla: Permiso

-- Usuario: 0001
INSERT INTO dbo.Permiso VALUES( '0001', 1, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0001', 2, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0001', 3, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0001', 4, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0001', 5, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0001', 6, 'ACTIVO');

-- Usuario: 0002
INSERT INTO dbo.Permiso VALUES( '0002', 1, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0002', 2, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0002', 3, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0002', 4, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0002', 5, 'CANCELADO');
INSERT INTO dbo.Permiso VALUES( '0002', 6, 'CANCELADO');

-- Usuario: 0003
INSERT INTO dbo.Permiso VALUES( '0003', 1, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0003', 2, 'CANCELADO');
INSERT INTO dbo.Permiso VALUES( '0003', 3, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0003', 4, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0003', 5, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0003', 6, 'CANCELADO');

-- Usuario: 0004
INSERT INTO dbo.Permiso VALUES( '0004', 1, 'CANCELADO');
INSERT INTO dbo.Permiso VALUES( '0004', 2, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0004', 3, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0004', 4, 'CANCELADO');
INSERT INTO dbo.Permiso VALUES( '0004', 5, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0004', 6, 'CANCELADO');

-- Usuario: 0005
INSERT INTO dbo.Permiso VALUES( '0005', 1, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0005', 2, 'CANCELADO');
INSERT INTO dbo.Permiso VALUES( '0005', 3, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0005', 4, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0005', 5, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0005', 6, 'CANCELADO');

-- Usuario: 0006
INSERT INTO dbo.Permiso VALUES( '0006', 1, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0006', 2, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0006', 3, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0006', 4, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0006', 5, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0006', 6, 'ACTIVO');

-- Usuario: 0007
INSERT INTO dbo.Permiso VALUES( '0007', 1, 'CANCELADO');
INSERT INTO dbo.Permiso VALUES( '0007', 2, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0007', 3, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0007', 4, 'CANCELADO');
INSERT INTO dbo.Permiso VALUES( '0007', 5, 'ACTIVO');
INSERT INTO dbo.Permiso VALUES( '0007', 6, 'CANCELADO');


-- Asignado
INSERT INTO dbo.Asignado values( '000001', '001', '0004', '20071115', null );
INSERT INTO dbo.Asignado values( '000002', '002', '0001', '20071120', null );
INSERT INTO dbo.Asignado values( '000003', '003', '0002', '20071128', null );
INSERT INTO dbo.Asignado values( '000004', '004', '0003', '20071212', '20080325' );
INSERT INTO dbo.Asignado values( '000005', '005', '0006', '20071220', null );
INSERT INTO dbo.Asignado values( '000006', '006', '0005', '20080105', null );
INSERT INTO dbo.Asignado values( '000007', '004', '0007', '20080107', null );
INSERT INTO dbo.Asignado values( '000008', '005', '0008', '20080107', null );
INSERT INTO dbo.Asignado values( '000009', '001', '0011', '20080108', null );
INSERT INTO dbo.Asignado values( '000010', '002', '0009', '20080108', null );
INSERT INTO dbo.Asignado values( '000011', '006', '0010', '20080108', null );

-- Tabla: Parametro
INSERT INTO dbo.Parametro values( '001', 'ITF - Impuesto a la Transacciones Financieras', '0.08', 'ACTIVO' );
INSERT INTO dbo.Parametro values( '002', 'Número de Operaciones Sin Costo', '15', 'ACTIVO' );

-- Tabla: Cliente
INSERT INTO dbo.Cliente values( '00001', 'CORONEL', 'CASTILLO', 'ERIC GUSTAVO', '06914897', 'LIMA', 'LOS OLIVOS', '996-664-457', 'gcoronelc@gmail.com' );
INSERT INTO dbo.Cliente values( '00002', 'VALENCIA', 'MORALES', 'PEDRO HUGO', '01576173', 'LIMA', 'MAGDALENA', '924-7834', 'pvalencia@terra.com.pe' );
INSERT INTO dbo.Cliente values( '00003', 'MARCELO', 'VILLALOBOS', 'RICARDO', '10762367', 'LIMA', 'LINCE', '993-62966', 'ricardomarcelo@hotmail.com' );
INSERT INTO dbo.Cliente values( '00004', 'ROMERO', 'CASTILLO', 'CARLOS ALBERTO', '06531983', 'LIMA', 'LOS OLIVOS', '865-84762', 'c.romero@hotmail.com' );
INSERT INTO dbo.Cliente values( '00005', 'ARANDA', 'LUNA', 'ALAN ALBERTO', '10875611', 'LIMA', 'SAN ISIDRO', '834-67125', 'a.aranda@hotmail.com' );
INSERT INTO dbo.Cliente values( '00006', 'AYALA', 'PAZ', 'JORGE LUIS', '10679245', 'LIMA', 'SAN BORJA', '963-34769', 'j.ayala@yahoo.com' );
INSERT INTO dbo.Cliente values( '00007', 'CHAVEZ', 'CANALES', 'EDGAR RAFAEL', '10145693', 'LIMA', 'MIRAFLORES', '999-96673', 'e.chavez@gmail.com' );
INSERT INTO dbo.Cliente values( '00008', 'FLORES', 'CHAFLOQUE', 'ROSA LIZET', '10773456', 'LIMA', 'LA MOLINA', '966-87567', 'r.florez@hotmail.com' );
INSERT INTO dbo.Cliente values( '00009', 'FLORES', 'CASTILLO', 'CRISTIAN RAFAEL', '10346723', 'LIMA', 'LOS OLIVOS', '978-43768', 'c.flores@hotmail.com' );
INSERT INTO dbo.Cliente values( '00010', 'GONZALES', 'GARCIA', 'GABRIEL ALEJANDRO', '10192376', 'LIMA', 'SAN MIGUEL', '945-56782', 'g.gonzales@yahoo.es' );
INSERT INTO dbo.Cliente values( '00011', 'LAY', 'VALLEJOS', 'JUAN CARLOS', '10942287', 'LIMA', 'LINCE', '956-12657', 'j.lay@peru.com' );
INSERT INTO dbo.Cliente values( '00012', 'MONTALVO', 'SOTO', 'DEYSI LIDIA', '10612376', 'LIMA', 'SURCO', '965-67235', 'd.montalvo@hotmail.com' );
INSERT INTO dbo.Cliente values( '00013', 'RICALDE', 'RAMIREZ', 'ROSARIO ESMERALDA', '10761324', 'LIMA', 'MIRAFLORES', '991-23546', 'r.ricalde@gmail.com' );
INSERT INTO dbo.Cliente values( '00014', 'RODRIGUEZ', 'FLORES', 'ENRIQUE MANUEL', '10773345', 'LIMA', 'LINCE', '976-82838', 'e.rodriguez@gmail.com' );
INSERT INTO dbo.Cliente values( '00015', 'ROJAS', 'OSCANOA', 'FELIX NINO', '10238943', 'LIMA', 'LIMA', '962-32158', 'f.rojas@yahoo.com' );
INSERT INTO dbo.Cliente values( '00016', 'TEJADA', 'DEL AGUILA', 'TANIA LORENA', '10446791', 'LIMA', 'PUEBLO LIBRE', '966-23854', 't.tejada@hotmail.com' );
INSERT INTO dbo.Cliente values( '00017', 'VALDEVIESO', 'LEYVA', 'LIDIA ROXANA', '10452682', 'LIMA', 'SURCO', '956-78951', 'r.valdivieso@terra.com.pe' );
INSERT INTO dbo.Cliente values( '00018', 'VALENTIN', 'COTRINA', 'JUAN DIEGO', '10398247', 'LIMA', 'LA MOLINA', '921-12456', 'j.valentin@terra.com.pe' );
INSERT INTO dbo.Cliente values( '00019', 'YAURICASA', 'BAUTISTA', 'YESABETH', '10934584', 'LIMA', 'MAGDALENA', '977-75777', 'y.yauricasa@terra.com.pe' );
INSERT INTO dbo.Cliente values( '00020', 'ZEGARRA', 'GARCIA', 'FERNANDO MOISES', '10772365', 'LIMA', 'SAN ISIDRO', '936-45876', 'f.zegarra@hotmail.com' );

-- Tabla: Cuenta
INSERT INTO dbo.Cuenta values('00200001','01','002','0001','00008',7000,'20080105','ACTIVO',15,'123456');
INSERT INTO dbo.Cuenta values('00200002','01','002','0001','00001',6800,'20080109','ACTIVO',3,'123456');
INSERT INTO dbo.Cuenta values('00200003','02','002','0001','00007',6000,'20080111','ACTIVO',6,'123456');
INSERT INTO dbo.Cuenta values('00100001','01','001','0004','00005',6900,'20080106','ACTIVO',7,'123456');
INSERT INTO dbo.Cuenta values('00100002','02','001','0004','00005',4500,'20080108','ACTIVO',4,'123456');
INSERT INTO dbo.Cuenta values('00300001','01','003','0002','00010',0000,'20080107','CANCELADO',3,'123456');


-- Tabla: Movimiento
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00100002',01,'20080108','0004','001',1800,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00100002',02,'20080125','0004','004',1000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00100002',03,'20080213','0004','003',2200,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00100002',04,'20080308','0004','003',1500,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00100001',01,'20080106','0004','001',2800,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00100001',02,'20080115','0004','003',3200,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00100001',03,'20080120','0004','004',0800,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00100001',04,'20080214','0004','003',2000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00100001',05,'20080225','0004','004',0500,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00100001',06,'20080303','0004','004',0800,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00100001',07,'20080315','0004','003',1000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200003',01,'20080111','0001','001',2500,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200003',02,'20080117','0001','003',1500,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200003',03,'20080120','0001','004',0500,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200003',04,'20080209','0001','004',0500,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200003',05,'20080225','0001','003',3500,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200003',06,'20080311','0001','004',0500,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200002',01,'20080109','0001','001',3800,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200002',02,'20080120','0001','003',4200,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200002',03,'20080306','0001','004',1200,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200001',01,'20080105','0001','001',5000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200001',02,'20080107','0001','003',4000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200001',03,'20080109','0001','004',2000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200001',04,'20080111','0001','003',1000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200001',05,'20080113','0001','003',2000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200001',06,'20080115','0001','004',4000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200001',07,'20080119','0001','003',2000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200001',08,'20080121','0001','004',3000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200001',09,'20080123','0001','003',7000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200001',10,'20080127','0001','004',1000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200001',11,'20080130','0001','004',3000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200001',12,'20080204','0001','003',2000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200001',13,'20080208','0001','004',4000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200001',14,'20080213','0001','003',2000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00200001',15,'20080219','0001','004',1000,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00300001',01,'20080107','0002','001',5600,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00300001',02,'20080118','0002','003',1400,null);
INSERT INTO dbo.Movimiento (chr_cuencodigo,int_movinumero,dtt_movifecha,chr_emplcodigo,chr_tipocodigo,dec_moviimporte,chr_cuenreferencia) VALUES ('00300001',03,'20080125','0002','002',7000,null);


--  Tabla: Contador
INSERT INTO dbo.Contador Values( 'Moneda', 2, 2 );
INSERT INTO dbo.Contador Values( 'TipoMovimiento', 10, 3 );
INSERT INTO dbo.Contador Values( 'Sucursal', 7, 3 );
INSERT INTO dbo.Contador Values( 'Empleado', 14, 4 );
INSERT INTO dbo.Contador Values( 'Asignado', 11, 6 );
INSERT INTO dbo.Contador Values( 'Parametro', 2, 3 );
INSERT INTO dbo.Contador Values( 'Cliente', 20, 5 );
END;
COMMIT TRANSACTION;
GO
