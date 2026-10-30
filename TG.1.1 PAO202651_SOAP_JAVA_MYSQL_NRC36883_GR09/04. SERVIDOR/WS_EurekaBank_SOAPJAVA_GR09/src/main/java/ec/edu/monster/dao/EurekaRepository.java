/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.dao;

import ec.edu.monster.db.AccesoDB;
import ec.edu.monster.model.DatosCuenta;
import ec.edu.monster.model.MovimientoData;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author leona
 */
public class EurekaRepository {

    /**
     * Devuelve todas las cuentas activas con datos del cliente (formato
     * simplificado)
     */
    public List<DatosCuenta> leerCuentasConClientes() {
        List<DatosCuenta> lista = new ArrayList<>();
        String sql = "SELECT "
                + "c.chr_cuencodigo, "
                + "c.chr_monecodigo, "
                + "c.dec_cuensaldo, "
                + "c.vch_cuenestado, "
                + "cl.vch_cliepaterno, "
                + "cl.vch_cliematerno, "
                + "cl.vch_clienombre, "
                + "cl.vch_clieemail, "
                + "cl.vch_clietelefono "
                + "FROM Cuenta c "
                + "INNER JOIN Cliente cl ON c.chr_cliecodigo = cl.chr_cliecodigo "
                + "WHERE c.vch_cuenestado = 'ACTIVO' "
                + "ORDER BY c.chr_cuencodigo";

        try (Connection cn = AccesoDB.getConnection(); PreparedStatement pstm = cn.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {

            while (rs.next()) {
                DatosCuenta dc = new DatosCuenta();

                // Campos correctos de DatosCuenta
                dc.setCodigo(rs.getString("chr_cuencodigo"));
                dc.setMoneda(rs.getString("chr_monecodigo"));
                dc.setSaldo(rs.getBigDecimal("dec_cuensaldo"));
                dc.setEstado(rs.getString("vch_cuenestado"));

                // Nombre completo
                String nombreCompleto = (rs.getString("vch_cliepaterno") + " "
                        + rs.getString("vch_cliematerno") + ", "
                        + rs.getString("vch_clienombre")).trim();
                dc.setNombreCliente(nombreCompleto);

                dc.setEmailCliente(rs.getString("vch_clieemail"));
                dc.setTelefonoCliente(rs.getString("vch_clietelefono"));

                lista.add(dc);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener cuentas con clientes: " + e.getMessage(), e);
        }
        return lista;
    }

    // Opcional: por código de cliente
    public List<DatosCuenta> leerCuentasPorCliente(String clienteCodigo) {
        List<DatosCuenta> lista = new ArrayList<>();
        String sql = "SELECT c.chr_cuencodigo, c.chr_monecodigo, c.chr_sucucodigo, "
                + "c.chr_emplcreacuenta, c.chr_cliecodigo, c.dec_cuensaldo, "
                + "c.dtt_cuenfechacreacion, c.vch_cuenestado, c.int_cuencontmov, "
                + "cl.vch_cliepaterno, cl.vch_cliematerno, cl.vch_clienombre, "
                + "cl.chr_cliedni, cl.vch_clieciudad, cl.vch_cliedireccion, "
                + "cl.vch_clietelefono, cl.vch_clieemail "
                + "FROM Cuenta c "
                + "INNER JOIN Cliente cl ON c.chr_cliecodigo = cl.chr_cliecodigo "
                + "WHERE c.vch_cuenestado = 'ACTIVO' "
                + "ORDER BY c.chr_cuencodigo";
        try (Connection cn = AccesoDB.getConnection(); PreparedStatement pstm = cn.prepareStatement(sql)) {

            pstm.setString(1, clienteCodigo);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    DatosCuenta cc = new DatosCuenta();
                    // ... (mismo mapeo que arriba)
                    lista.add(cc);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cuentas del cliente " + clienteCodigo + ": " + e.getMessage(), e);
        }
        return lista;
    }

    public List<MovimientoData> leerMovimientos(String cuenta) {
        List<MovimientoData> lista = new ArrayList<>();
        Map<String, List<MovimientoData>> grupos = new HashMap<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
                .append("  m.chr_cuencodigo AS cuenta, ")
                .append("  m.int_movinumero AS numero, ")
                .append("  m.dtt_movifecha AS fecha, ")
                .append("  t.vch_tipodescripcion AS descripcion, ")
                .append("  t.vch_tipoaccion AS accion, ")
                .append("  m.dec_moviimporte AS importe, ")
                .append("  m.chr_cuenreferencia AS referencia, ")
                .append("  cl.vch_cliepaterno, ")
                .append("  cl.vch_cliematerno, ")
                .append("  cl.vch_clienombre ")
                .append("FROM Movimiento m ")
                .append("INNER JOIN TipoMovimiento t ON m.chr_tipocodigo = t.chr_tipocodigo ")
                .append("INNER JOIN Cuenta c ON m.chr_cuencodigo = c.chr_cuencodigo ")
                .append("INNER JOIN Cliente cl ON c.chr_cliecodigo = cl.chr_cliecodigo ");

        if (cuenta != null && !cuenta.trim().isEmpty()) {
            sql.append("WHERE m.chr_cuencodigo = ? ");
        }
        sql.append("ORDER BY m.dtt_movifecha ASC, m.int_movinumero ASC");

        try (Connection cn = AccesoDB.getConnection(); PreparedStatement pstm = cn.prepareStatement(sql.toString())) {

            if (cuenta != null && !cuenta.trim().isEmpty()) {
                pstm.setString(1, cuenta);
            }

            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    MovimientoData md = new MovimientoData();

                    String cuentaKey = rs.getString("cuenta");
                    md.setCodigoCuenta(cuentaKey);
                    md.setNumero(rs.getInt("numero"));
                    md.setFecha(rs.getDate("fecha"));
                    md.setImporte(rs.getBigDecimal("importe"));

                    // Nombre completo
                    String nombre = (rs.getString("vch_cliepaterno") + " "
                            + rs.getString("vch_cliematerno") + ", "
                            + rs.getString("vch_clienombre")).trim();
                    md.setNombreCliente(nombre);

                    // Tipo con acción
                    String accion = rs.getString("accion");
                    String tipoBase = rs.getString("descripcion");
                    String tipoFinal = "INGRESO".equals(accion) ? tipoBase + " Entrada" : tipoBase + " Salida";
                    md.setTipo(tipoFinal);

                    // Referencia
                    String ref = rs.getString("referencia");
                    md.setReferencia(ref != null && !ref.trim().isEmpty() ? ref.trim() : null);

                    // Agrupar
                    grupos.computeIfAbsent(cuentaKey, k -> new ArrayList<>()).add(md);
                }
            }

            // Calcular saldo acumulado
            for (List<MovimientoData> grupo : grupos.values()) {
                BigDecimal saldoAcumulado = BigDecimal.ZERO;

                for (MovimientoData md : grupo) {
                    String accion = md.getTipo().contains("Entrada") ? "INGRESO" : "SALIDA";
                    BigDecimal importe = md.getImporte();
                    BigDecimal movimiento = "INGRESO".equals(accion) ? importe : importe.negate();

                    saldoAcumulado = saldoAcumulado.add(movimiento);
                    md.setSaldoActual(saldoAcumulado);
                }

                // Orden descendente final
                grupo.sort((a, b) -> {
                    int fechaComp = b.getFecha().compareTo(a.getFecha());
                    if (fechaComp != 0) {
                        return fechaComp;
                    }
                    return Integer.compare(b.getNumero(), a.getNumero());
                });

                lista.addAll(grupo);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al leer movimientos: " + e.getMessage(), e);
        }

        return lista;
    }

    public void registrarDeposito(String cuenta, double importe, String codEmp) {
        realizarMovimiento(cuenta, importe, codEmp, "DEPOSITO", null);
    }

    public void registrarRetiro(String cuenta, double importe, String codEmp) {
        realizarMovimiento(cuenta, importe, codEmp, "RETIRO", null);
    }

    public void registrarTransferencia(String cuentaOrigen, String cuentaDestino, double importe, String codEmp) {
        realizarMovimiento(cuentaOrigen, importe, codEmp, "TRANSFERENCIA", cuentaDestino);
    }
    // Códigos de tipo de movimiento
    private static final String TIPO_DEPOSITO = "003";
    private static final String TIPO_RETIRO = "004";
    private static final String TIPO_TRANSF_INGRESO = "008";
    private static final String TIPO_TRANSF_SALIDA = "009";

    /**
     * Realiza depósito, retiro o transferencia
     *
     * @param cuentaOrigen Cuenta origen
     * @param importe Importe (positivo)
     * @param codEmp Código del empleado
     * @param tipo Tipo: "DEPOSITO", "RETIRO", "TRANSFERENCIA"
     * @param cuentaDestino Solo para transferencia
     */
    public void realizarMovimiento(String cuentaOrigen, double importe, String codEmp, String tipo, String cuentaDestino) {
        if (importe <= 0) {
            throw new RuntimeException("El importe debe ser mayor a 0.");
        }

        Connection cn = null;
        try {
            cn = AccesoDB.getConnection();
            cn.setAutoCommit(false);

            // Validar empleado
            validarEmpleado(cn, codEmp);

            boolean esDeposito = "DEPOSITO".equalsIgnoreCase(tipo);
            boolean esRetiro = "RETIRO".equalsIgnoreCase(tipo);
            boolean esTransferencia = "TRANSFERENCIA".equalsIgnoreCase(tipo);

            if (!esDeposito && !esRetiro && !esTransferencia) {
                throw new RuntimeException("Tipo inválido: " + tipo);
            }

            if (esTransferencia && (cuentaDestino == null || cuentaDestino.trim().isEmpty())) {
                throw new RuntimeException("Cuenta destino requerida");
            }

            // === DEPÓSITO O RETIRO ===
            if (esDeposito || esRetiro) {
                boolean esIngreso = esDeposito;
                String tipoCodigo = esDeposito ? TIPO_DEPOSITO : TIPO_RETIRO;
                procesarCuenta(cn, cuentaOrigen, importe, codEmp, tipoCodigo, esIngreso, null);
            }

            // === TRANSFERENCIA ===
            if (esTransferencia) {
                if (cuentaOrigen.equals(cuentaDestino)) {
                    throw new RuntimeException("No se puede transferir a la misma cuenta.");
                }
                procesarCuenta(cn, cuentaOrigen, importe, codEmp, TIPO_TRANSF_SALIDA, false, cuentaDestino);
                procesarCuenta(cn, cuentaDestino, importe, codEmp, TIPO_TRANSF_INGRESO, true, cuentaOrigen);
            }

            cn.commit();
        } catch (SQLException e) {
            rollback(cn);
            throw new RuntimeException("Error en BD: " + e.getMessage());
        } catch (Exception e) {
            rollback(cn);
            throw new RuntimeException("Error: " + e.getMessage());
        } finally {
            close(cn);
        }
    }

    // === MÉTODO REUTILIZABLE: procesar una cuenta (origen o destino) ===
    private void procesarCuenta(Connection cn, String cuenta, double importe, String codEmp,
            String tipoCodigo, boolean esIngreso, String cuentaRef) throws SQLException {

        // Paso 1: Leer cuenta con FOR UPDATE
        String sql = "SELECT dec_cuensaldo, int_cuencontmov, vch_cuenestado "
                + "FROM Cuenta WHERE chr_cuencodigo = ? FOR UPDATE";
        PreparedStatement pstm = cn.prepareStatement(sql);
        pstm.setString(1, cuenta);
        ResultSet rs = pstm.executeQuery();

        if (!rs.next()) {
            throw new SQLException("Cuenta no existe: " + cuenta);
        }

        String estado = rs.getString("vch_cuenestado");
        if (!"ACTIVO".equals(estado)) {
            throw new SQLException("Cuenta no está activa: " + cuenta);
        }

        double saldo = rs.getDouble("dec_cuensaldo");
        int cont = rs.getInt("int_cuencontmov");
        rs.close();
        pstm.close();

        // Validar saldo en retiro/salida
        if (!esIngreso && saldo < importe) {
            throw new SQLException("Saldo insuficiente en cuenta: " + cuenta);
        }

        // Paso 2: Siguiente número de movimiento
        int nroMov = obtenerSiguienteMovimiento(cn, cuenta);

        // Paso 3: Actualizar saldo y contador
        double nuevoSaldo = esIngreso ? saldo + importe : saldo - importe;
        cont++;

        sql = "UPDATE Cuenta SET dec_cuensaldo = ?, int_cuencontmov = ? WHERE chr_cuencodigo = ?";
        pstm = cn.prepareStatement(sql);
        pstm.setDouble(1, nuevoSaldo);
        pstm.setInt(2, cont);
        pstm.setString(3, cuenta);
        pstm.executeUpdate();
        pstm.close();

        // Paso 4: Registrar movimiento
        sql = "INSERT INTO Movimiento(chr_cuencodigo, int_movinumero, dtt_movifecha, "
                + "chr_emplcodigo, chr_tipocodigo, dec_moviimporte, chr_cuenreferencia) "
                + "VALUES(?, ?, CURDATE(), ?, ?, ?, ?)";
        pstm = cn.prepareStatement(sql);
        pstm.setString(1, cuenta);
        pstm.setInt(2, nroMov);
        pstm.setString(3, codEmp);
        pstm.setString(4, tipoCodigo);
        pstm.setDouble(5, importe);
        pstm.setString(6, cuentaRef); // puede ser NULL
        pstm.executeUpdate();
        pstm.close();
    }

    // === Obtener siguiente número de movimiento ===
    private int obtenerSiguienteMovimiento(Connection cn, String cuenta) throws SQLException {
        String sql = "SELECT COALESCE(MAX(int_movinumero), 0) + 1 AS nro FROM Movimiento WHERE chr_cuencodigo = ?";
        PreparedStatement pstm = cn.prepareStatement(sql);
        pstm.setString(1, cuenta);
        ResultSet rs = pstm.executeQuery();
        int nro = 1;
        if (rs.next()) {
            nro = rs.getInt("nro");
        }
        rs.close();
        pstm.close();
        return nro;
    }

    private void validarEmpleado(Connection cn, String codEmp) throws SQLException {
        String sql = "SELECT vch_emplpaterno FROM Empleado WHERE chr_emplcodigo = ?";
        try (PreparedStatement pstm = cn.prepareStatement(sql)) {
            pstm.setString(1, codEmp);
            try (ResultSet rs = pstm.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Empleado no existe: " + codEmp);
                }
            }
        }

        // Opcional: validar estado en Usuario
        String sql2 = "SELECT vch_emplestado FROM Usuario WHERE chr_emplcodigo = ? AND vch_emplestado = 'ACTIVO'";
        try (PreparedStatement pstm = cn.prepareStatement(sql2)) {
            pstm.setString(1, codEmp);
            try (ResultSet rs = pstm.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Empleado no está activo o no tiene usuario: " + codEmp);
                }
            }
        }
    }

    // === Utilidades ===
    private void rollback(Connection cn) {
        if (cn != null) {
            try {
                cn.rollback();
            } catch (Exception e) {
            }
        }
    }

    private void close(Connection cn) {
        if (cn != null) {
            try {
                cn.close();
            } catch (Exception e) {
            }
        }
    }
}

