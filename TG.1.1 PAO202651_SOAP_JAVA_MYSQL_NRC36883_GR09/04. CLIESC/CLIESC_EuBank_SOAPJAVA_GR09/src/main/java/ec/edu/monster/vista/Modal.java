
package ec.edu.monster.vista;

import ec.edu.monster.controladores.SwingController;
import ec.edu.monster.ws.eurekabank.ResultadoOperacion;
import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.Image;
import javax.swing.JOptionPane;

/**
 *
 * @author Dome
 */
public class Modal extends javax.swing.JDialog {

    // --- ¡CAMBIO 1: Añadir el servicio! ---
    private final SwingController controller;
    
    // Variable para saber si la operación fue exitosa
    private boolean operacionExitosa = false;
    /**
     * Creates new form Modal
     */
    public Modal(java.awt.Frame parent, boolean modal) {
        this(parent, modal, new SwingController(new ec.edu.monster.servicios.EuBankService()));
    }

    public Modal(java.awt.Frame parent, boolean modal, SwingController controller) {
        super(parent, modal); // <-- ¡La llamada clave!
        this.controller = controller;
        initComponents();
        
        // 5. Mueve todo tu código del constructor anterior aquí
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Usa JDialog.DISPOSE...
        // pack(); // pack() ya está al final de initComponents()
        setLocationRelativeTo(parent); // Centrar relativo al PADRE, no a null
        
        cbx_movement.addItem("DEPOSITO");
        cbx_movement.addItem("RETIRO");
        cbx_movement.addItem("TRANSFERENCIA");
        cbx_movement.setSelectedIndex(0); // opcional
        
        
        lblTitle3.setVisible(false);
        txt_destino.setVisible(false);
    }
    
    
    private String seleccion = ""; 

    // Método para obtener el resultado
    public String getSeleccion() {
        return (String) cbx_movement.getSelectedItem();
    }
    
    public String getCuentaOrigen() {
        return txt_origen.getText();
    }

    public String getCuentaDestino() {
        return txt_destino.getText();
    }
    
    public double getImporte() {
         try {
            // Asegúrate de tener un JTextField llamado 'txt_importe'
            return Double.parseDouble(txt_importe.getText().replace(',', '.'));
         } catch (Exception e) {
             return 0.0;
         }
    }
    
    public boolean isOperacionExitosa() {
        return operacionExitosa;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        modernPanel1 = new ec.edu.monster.vista.ModernPanel();
        lblTitle1 = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        lblTitle2 = new javax.swing.JLabel();
        lblTitle3 = new javax.swing.JLabel();
        cbx_movement = new ec.edu.monster.vista.Combobox();
        txt_origen = new javax.swing.JTextField();
        txt_destino = new javax.swing.JTextField();
        lblImagenes = new javax.swing.JLabel();
        btn_cancel = new ec.edu.monster.vista.ModernButton();
        btn_crear = new ec.edu.monster.vista.ModernButton();
        lblTitle4 = new javax.swing.JLabel();
        txt_importe = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        modernPanel1.setBackground(new java.awt.Color(239, 243, 248));
        modernPanel1.setBorder(null);
        modernPanel1.setBorderColor(new java.awt.Color(239, 243, 248));
        modernPanel1.setPanelBackground(new java.awt.Color(239, 243, 248));

        lblTitle1.setFont(new java.awt.Font("Britannic Bold", 0, 24)); // NOI18N
        lblTitle1.setForeground(new java.awt.Color(11, 31, 58));
        lblTitle1.setText("Crear Nuevo Movimiento");

        javax.swing.GroupLayout modernPanel1Layout = new javax.swing.GroupLayout(modernPanel1);
        modernPanel1.setLayout(modernPanel1Layout);
        modernPanel1Layout.setHorizontalGroup(
            modernPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modernPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblTitle1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        modernPanel1Layout.setVerticalGroup(
            modernPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modernPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle1)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        lblTitle.setFont(new java.awt.Font("Britannic Bold", 0, 16)); // NOI18N
        lblTitle.setText("Tipo de Movimiento");

        lblTitle2.setFont(new java.awt.Font("Britannic Bold", 0, 16)); // NOI18N
        lblTitle2.setText("Cuenta Origen");

        lblTitle3.setFont(new java.awt.Font("Britannic Bold", 0, 16)); // NOI18N
        lblTitle3.setText("Cuenta Destino");

        cbx_movement.setArrowColor(new java.awt.Color(239, 243, 248));
        cbx_movement.setLineColor(new java.awt.Color(239, 243, 248));
        cbx_movement.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        cbx_movement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbx_movementActionPerformed(evt);
            }
        });

        txt_origen.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N

        txt_destino.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N

        btn_cancel.setBackgroundColor(new java.awt.Color(153, 153, 153));
        btn_cancel.setBorder(null);
        btn_cancel.setBorderColor(new java.awt.Color(153, 153, 153));
        btn_cancel.setBorderRadius(0);
        btn_cancel.setBorderThickness(0);
        btn_cancel.setHoverColor(new java.awt.Color(102, 102, 102));
        btn_cancel.setText("Cancelar");
        btn_cancel.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        btn_cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cancelActionPerformed(evt);
            }
        });

        btn_crear.setBackgroundColor(new java.awt.Color(11, 31, 58));
        btn_crear.setBorder(null);
        btn_crear.setBorderColor(new java.awt.Color(11, 31, 58));
        btn_crear.setBorderRadius(0);
        btn_crear.setBorderThickness(0);
        btn_crear.setHoverColor(new java.awt.Color(22, 58, 95));
        btn_crear.setText("Crear Movimiento");
        btn_crear.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        btn_crear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_crearActionPerformed(evt);
            }
        });

        lblTitle4.setFont(new java.awt.Font("Britannic Bold", 0, 16)); // NOI18N
        lblTitle4.setText("Importe");

        txt_importe.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(modernPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(171, 171, 171)
                .addComponent(btn_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_crear, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitle4)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txt_importe, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblTitle, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblTitle2, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblTitle3, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cbx_movement, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                        .addComponent(txt_origen, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txt_destino, javax.swing.GroupLayout.Alignment.LEADING)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addComponent(lblImagenes, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(modernPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblTitle)
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cbx_movement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTitle2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_origen, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblTitle3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txt_destino, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblTitle4))
                    .addComponent(lblImagenes, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_importe, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_cancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_crear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cancelActionPerformed
        seleccion = null; // Cancela
        this.operacionExitosa = false;
        this.dispose();
    }//GEN-LAST:event_btn_cancelActionPerformed

    private void btn_crearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_crearActionPerformed
        // ¡Lee los valores en el momento!
        String tipo = getSeleccion();
        String origen = getCuentaOrigen();
        String destino = getCuentaDestino();
        double importe = getImporte();

        // --- Validaciones ---
        if (tipo == null || tipo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un tipo de movimiento.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
         if (importe <= 0) {
            JOptionPane.showMessageDialog(this, "El importe debe ser mayor a 0.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (origen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La cuenta de origen es obligatoria.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (tipo.equals("TRANSFERENCIA") && destino.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La cuenta de destino es obligatoria para transferencias.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!tipo.equals("TRANSFERENCIA")) {
            destino = ""; // Asegura que destino esté vacío si no es transferencia
        }

        // --- Llamada al Servicio ---
        try {
            // ¡Usa el servicio que recibiste en el constructor!
            ResultadoOperacion res = this.controller.registrarMovimiento(tipo, origen, destino, importe);

            if (res.getCodigo() == 1) { // 1 = Éxito
                JOptionPane.showMessageDialog(this, "¡Operación exitosa!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                this.operacionExitosa = true; // ¡Importante!
                this.dispose(); // Cierra el modal
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + res.getMensaje(), "Error de Operación", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_crearActionPerformed

    private void cbx_movementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbx_movementActionPerformed
        String opcion = (String) cbx_movement.getSelectedItem();
        if (opcion == null) return;

        // ¡TU LÓGICA!
        if (opcion.equals("TRANSFERENCIA")) {
            lblTitle3.setVisible(true); // Muestra "Cuenta Destino"
            txt_destino.setVisible(true);
        } else {
            lblTitle3.setVisible(false); // Oculta "Cuenta Destino"
            txt_destino.setVisible(false);
            txt_destino.setText(""); // Limpia el campo por si acaso
        }

        // Lógica de imágenes
        try {
            switch (opcion) {
                case "DEPOSITO" -> // (Asegúrate que coincida con el texto del ComboBox)
                    lblImagenes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Depósito.png")));
                case "RETIRO" ->
                    lblImagenes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Retiro.png")));
                case "TRANSFERENCIA" ->
                    lblImagenes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Transferencia.png")));
                default ->
                    lblImagenes.setIcon(null);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar imagen de movimiento: " + e.getMessage());
        }
    }//GEN-LAST:event_cbx_movementActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Modal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Modal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Modal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Modal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Modal(null, true).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ec.edu.monster.vista.ModernButton btn_cancel;
    private ec.edu.monster.vista.ModernButton btn_crear;
    private ec.edu.monster.vista.Combobox cbx_movement;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblImagenes;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTitle1;
    private javax.swing.JLabel lblTitle2;
    private javax.swing.JLabel lblTitle3;
    private javax.swing.JLabel lblTitle4;
    private ec.edu.monster.vista.ModernPanel modernPanel1;
    private javax.swing.JTextField txt_destino;
    private javax.swing.JTextField txt_importe;
    private javax.swing.JTextField txt_origen;
    // End of variables declaration//GEN-END:variables
}
