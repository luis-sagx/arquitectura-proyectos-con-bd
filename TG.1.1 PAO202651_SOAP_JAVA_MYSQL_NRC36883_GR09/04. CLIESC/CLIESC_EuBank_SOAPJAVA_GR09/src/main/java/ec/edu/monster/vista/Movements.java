package ec.edu.monster.vista;

import ec.edu.monster.controladores.SwingController;
import ec.edu.monster.servicios.EuBankService;
import ec.edu.monster.ws.eurekabank.DatosCuenta;
import ec.edu.monster.ws.eurekabank.MovimientoData;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Dome
 */
public class Movements extends javax.swing.JPanel {
    
    private SwingController controller;
    private final DecimalFormat decimalFormat;
    
    public Movements() {
        this(new SwingController(new EuBankService()));
    }

    public Movements(SwingController controller) {
        this.controller = controller;
        initComponents();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        this.decimalFormat = new DecimalFormat("#,##0.00", symbols);
        cargarDatosMovimientos();
    }
    // </editor-fold>
    
    public void setEuBankService(EuBankService euBankService) {
        this.controller = new SwingController(euBankService);
        cargarDatosMovimientos();
    }

@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        modernPanel2 = new ec.edu.monster.vista.ModernPanel();
        lblTitle = new javax.swing.JLabel();
        lblTitle1 = new javax.swing.JLabel();
        btn_crear = new ec.edu.monster.vista.ModernButton();
        tableMovements = new ec.edu.monster.vista.ModernTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(804, 650));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        modernPanel2.setBackground(new java.awt.Color(239, 243, 248));
        modernPanel2.setBorderColor(new java.awt.Color(255, 255, 255));
        modernPanel2.setPanelBackground(new java.awt.Color(248, 250, 252));

        lblTitle.setFont(new java.awt.Font("Britannic Bold", 0, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(11, 31, 58));
        lblTitle.setText("Movimientos Bancarios");

        lblTitle1.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        lblTitle1.setForeground(new java.awt.Color(102, 102, 102));
        lblTitle1.setText("Revisa los movimientos realizados entre las cuentas bancarias");

        btn_crear.setBackgroundColor(new java.awt.Color(11, 31, 58));
        btn_crear.setBorder(null);
        btn_crear.setBorderColor(new java.awt.Color(239, 243, 248));
        btn_crear.setBorderThickness(0);
        btn_crear.setHoverColor(new java.awt.Color(22, 58, 95));
        btn_crear.setText("+ Crear");
        btn_crear.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        btn_crear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_crearMouseClicked(evt);
            }
        });
        btn_crear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_crearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout modernPanel2Layout = new javax.swing.GroupLayout(modernPanel2);
        modernPanel2.setLayout(modernPanel2Layout);
        modernPanel2Layout.setHorizontalGroup(
            modernPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modernPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(modernPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle1)
                    .addComponent(lblTitle))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(btn_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        modernPanel2Layout.setVerticalGroup(
            modernPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modernPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(modernPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, false)
                    .addComponent(btn_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(modernPanel2Layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTitle1)))
                .addGap(8, 8, 8))
        );

        tableMovements.setBackground(new java.awt.Color(255, 255, 255));
        tableMovements.setBorder(null);
        tableMovements.setBorderRadius(0);
        tableMovements.setContainerBackground(new java.awt.Color(255, 255, 255));
        tableMovements.setEvenRowColor(new java.awt.Color(244, 247, 251));
        tableMovements.setHeaderBackground(new java.awt.Color(11, 31, 58));
        tableMovements.setHoverRowColor(new java.awt.Color(230, 238, 247));
        tableMovements.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        tableMovements.setName(""); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableMovements, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(modernPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(24, 24))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(modernPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(tableMovements, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 804, 650));
    }// </editor-fold>//GEN-END:initComponents
    
    private void cargarDatosMovimientos() {
        if (this.controller == null) {
            System.err.println("Error: SwingController no fue inyectado en el panel Movements.");
            return;
        }

        String[] columnNames = {"Cuenta", "Nro", "Fecha", "Tipo", "Importe", "Saldo"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        try {
            // Lógica para traer TODOS los movimientos (igual que en consola)
            List<DatosCuenta> cuentas = this.controller.traerCuentas();
            List<MovimientoData> todosMovimientos = new ArrayList<>();
            for (DatosCuenta cuenta : cuentas) {
                todosMovimientos.addAll(this.controller.traerMovimientos(cuenta.getCodigo()));
            }

            // Ordena por fecha y número
            todosMovimientos.sort((m1, m2) -> {
                int resFecha = m2.getFecha().toGregorianCalendar().compareTo(m1.getFecha().toGregorianCalendar());
                if (resFecha != 0) return resFecha;
                return Integer.compare(m2.getNumero(), m1.getNumero());
            });

            // Llena la tabla
            for (MovimientoData mov : todosMovimientos) {
                Object[] row = new Object[] {
                    mov.getCodigoCuenta(),
                    mov.getNumero(),
                    mov.getFecha().toString().substring(0, 10), // Acorta la fecha
                    mov.getTipo(),
                    decimalFormat.format(mov.getImporte()),
                    decimalFormat.format(mov.getSaldoActual())
                };
                model.addRow(row);
            }
            
            tableMovements.getTable().setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar movimientos: " + ex.getMessage(), "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btn_crearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_crearMouseClicked

    }//GEN-LAST:event_btn_crearMouseClicked

    private void btn_crearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_crearActionPerformed
        // 1. Obtener la ventana padre (el JFrame que contiene este JPanel)
        java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);

        // 2. Crear el JDialog modal, pasándole el padre y 'true'
        //    ¡Aquí usamos el nuevo constructor!
        Modal modal = new Modal((java.awt.Frame) parentWindow, true, controller);

        // 3. Mostrar el modal. El código se PAUSARÁ aquí
        //    hasta que el usuario cierre el diálogo.
        modal.setVisible(true);

        // 4. El código se reanuda AQUÍ cuando el modal se cierra.
        String tipoMovimiento = modal.getSeleccion();

        // 5. Verificar si el usuario presionó "Crear" (tipoMovimiento no será null)
        if (tipoMovimiento != null) {
            // El usuario NO canceló
            String origen = modal.getCuentaOrigen();
            String destino = modal.getCuentaDestino();

            // Imprimir en consola (para probar)
            System.out.println("--- Nuevo Movimiento ---");
            System.out.println("Tipo: " + tipoMovimiento);
            System.out.println("Origen: " + origen);
            System.out.println("Destino: " + destino);

            // TODO: Aquí es donde agregas los datos a tu 'tableMovements'
            // Por ejemplo:
            // javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) tableMovements.getModel();
            // model.addRow(new Object[]{"ID_NUEVO", tipoMovimiento, origen, destino});

        } else {
            // El usuario presionó "Cancelar"
            System.out.println("Operación cancelada.");
        }
    }//GEN-LAST:event_btn_crearActionPerformed
    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ec.edu.monster.vista.ModernButton btn_crear;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTitle1;
    private ec.edu.monster.vista.ModernPanel modernPanel2;
    private ec.edu.monster.vista.ModernTable tableMovements;
    // End of variables declaration//GEN-END:variables
}
