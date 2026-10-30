// Archivo: ModernTable.java
package ec.edu.monster.vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.beans.BeanProperty;

public class ModernTable extends JScrollPane {

    private final JTable table;

    // Colores personalizables
    private Color headerBackground = new Color(11, 31, 58);
    private Color headerForeground = Color.WHITE;
    private Color evenRowColor = new Color(250, 250, 250);
    private Color oddRowColor = Color.WHITE;
    private Color hoverRowColor = new Color(230, 238, 247);
    private Color selectionBackground = new Color(11, 31, 58);
    private Color selectionForeground = Color.WHITE;
    private Color containerBackground = new Color(245, 245, 245);
    private int borderRadius = 12;
    private boolean showGrid = false;

    public ModernTable() {
        this(new DefaultTableModel());
    }

    public ModernTable(TableModel model) {
        table = new JTable(model);
        initTable();
        setViewportView(table);
        getViewport().setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());
        setOpaque(false);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                repaint();
            }
        });
    }

    private void initTable() {
        table.setFillsViewportHeight(true);
        table.setRowHeight(40);
        table.setShowGrid(showGrid);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setBackground(headerBackground);
        header.setForeground(headerForeground);
        header.setFont(header.getFont().deriveFont(Font.BOLD));
        header.setPreferredSize(new Dimension(0, 40));

        table.setDefaultRenderer(Object.class, new ModernTableCellRenderer());

        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                table.repaint();
            }
        });
    }

    // --- GETTERS Y SETTERS ---
    @BeanProperty public Color getHeaderBackground() { return headerBackground; }
    public void setHeaderBackground(Color c) {
        this.headerBackground = c;
        if (table != null) table.getTableHeader().setBackground(c);
        repaint();
    }

    @BeanProperty public Color getHeaderForeground() { return headerForeground; }
    public void setHeaderForeground(Color c) {
        this.headerForeground = c;
        if (table != null) table.getTableHeader().setForeground(c);
        repaint();
    }

    @BeanProperty public Color getEvenRowColor() { return evenRowColor; }
    public void setEvenRowColor(Color c) { this.evenRowColor = c; repaint(); }

    @BeanProperty public Color getOddRowColor() { return oddRowColor; }
    public void setOddRowColor(Color c) { this.oddRowColor = c; repaint(); }

    @BeanProperty public Color getHoverRowColor() { return hoverRowColor; }
    public void setHoverRowColor(Color c) { this.hoverRowColor = c; repaint(); }

    @BeanProperty public Color getSelectionBackground() { return selectionBackground; }
    public void setSelectionBackground(Color c) {
        this.selectionBackground = c;
        table.setSelectionBackground(c);
        repaint();
    }

    @BeanProperty public Color getSelectionForeground() { return selectionForeground; }
    public void setSelectionForeground(Color c) {
        this.selectionForeground = c;
        table.setSelectionForeground(c);
        repaint();
    }

    @BeanProperty public Color getContainerBackground() { return containerBackground; }
    public void setContainerBackground(Color c) {
        this.containerBackground = c;
        setBackground(c);
        repaint();
    }

    @BeanProperty public int getBorderRadius() { return borderRadius; }
    public void setBorderRadius(int r) { this.borderRadius = Math.max(0, r); repaint(); }

    @BeanProperty public boolean isShowGrid() { return showGrid; }
    public void setShowGrid(boolean b) {
        this.showGrid = b;
        table.setShowGrid(b);
        repaint();
    }

    // --- Acceso a la tabla ---
    public JTable getTable() { return table; }

    // --- Renderer ---
    private class ModernTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

            label.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

            if (isSelected) {
                label.setBackground(selectionBackground);
                label.setForeground(selectionForeground);
            } else {
                int modelRow = table.convertRowIndexToModel(row);
                label.setBackground((modelRow % 2 == 0) ? evenRowColor : oddRowColor);
                label.setForeground(table.getForeground());
            }

            Point mouse = table.getMousePosition();
            if (mouse != null && !isSelected) {
                int hoverRow = table.rowAtPoint(mouse);
                if (hoverRow == row) {
                    label.setBackground(hoverRowColor);
                }
            }

            return label;
        }
    }

    // --- Fondo redondeado ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int arc = borderRadius * 2;

        g2.setColor(containerBackground);
        g2.fillRoundRect(0, 0, w, h, arc, arc);
        g2.dispose();
    }
}