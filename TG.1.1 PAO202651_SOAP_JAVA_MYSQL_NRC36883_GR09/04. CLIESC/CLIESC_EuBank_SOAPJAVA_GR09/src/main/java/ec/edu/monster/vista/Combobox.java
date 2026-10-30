package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.BasicStroke;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ListCellRenderer;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.beans.BeanProperty;

// --- CAMBIO 1: Se quitó <E> de la clase ---
public class Combobox extends JComboBox {

    // --- Propiedades Personalizables ---
    private String labeText = "Label"; // Texto del placeholder
    private Color lineColor = new Color(11, 31, 58); // Color del BORDE
    private Color arrowColor = new Color(150, 150, 150); // Color de la FLECHA

    // (El resto de getters y setters siguen igual)
    @BeanProperty(preferred = true, description = "Texto del placeholder")
    public String getLabeText() { return labeText; }
    @BeanProperty(preferred = true, description = "Texto del placeholder")
    public void setLabeText(String labeText) { this.labeText = labeText; repaint(); }
    @BeanProperty(preferred = true, description = "Color del borde")
    public Color getLineColor() { return lineColor; }
    @BeanProperty(preferred = true, description = "Color del borde")
    public void setLineColor(Color lineColor) { this.lineColor = lineColor; repaint(); }
    @BeanProperty(preferred = true, description = "Color de la flecha")
    public Color getArrowColor() { return arrowColor; }
    @BeanProperty(preferred = true, description = "Color de la flecha")
    public void setArrowColor(Color arrowColor) { this.arrowColor = arrowColor; repaint(); }

    // --- Constructor ---
    public Combobox() {
        setOpaque(false);
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 12, 10, 12)); 
        setUI(new ComboUI(this));
        setRenderer(new ModernListCellRenderer());
        setSelectedIndex(-1); 
    }

    // --- Renderer para los Items de la Lista (Dropdown) ---
    private class ModernListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(new EmptyBorder(8, 12, 8, 12));
            if (isSelected) {
                label.setBackground(lineColor); 
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(new Color(70, 70, 70));
            }
            return label;
        }
    }

    // --- UI Personalizada ---
    private class ComboUI extends BasicComboBoxUI {

        // --- CAMBIO 2: Se quitó <E> de la variable ---
        private Combobox combo;

        // --- CAMBIO 3: Se quitó <E> del constructor ---
        public ComboUI(Combobox combo) {
            this.combo = combo;
        }

        @Override
        protected JButton createArrowButton() {
            return new ArrowButton();
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = new BasicComboPopup(comboBox) {
                @Override
                protected JScrollPane createScroller() {
                    list.setFixedCellHeight(35);
                    list.setSelectionBackground(combo.getLineColor());
                    list.setSelectionForeground(Color.WHITE);
                    JScrollPane scroll = new JScrollPane(list);
                    scroll.setBackground(Color.WHITE);
                    scroll.setBorder(BorderFactory.createEmptyBorder());
                    return scroll;
                }
            };
            popup.setBorder(new LineBorder(new Color(200, 200, 200), 1));
            return popup;
        }
        
        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            
            // --- NUEVA VERSIÓN CORREGIDA ---
            // No llamamos al renderer, así evitamos el NullPointerException.
            
            Object selectedItem = comboBox.getSelectedItem();
            
            // Si no hay nada seleccionado (índice -1), no pintamos nada.
            // El placeholder se pinta en el método 'paint'
            if (selectedItem == null) {
                return; 
            }
            
            // Convertimos el ítem a String (esto es lo que haría el renderer básico)
            String text = selectedItem.toString(); 
            
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            
            // Usar la fuente y color del combobox (respeta el setFont)
            g2.setFont(comboBox.getFont());
            g2.setColor(comboBox.getForeground());
            
            // Calcular la posición del texto (centrado verticalmente)
            FontMetrics fm = g2.getFontMetrics();
            int yText = bounds.y + (bounds.height - fm.getHeight()) / 2 + fm.getAscent();
            
            // Dibujar el texto en la posición correcta
            g2.drawString(text, bounds.x, yText);
            
            g2.dispose();
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int w = getWidth(), h = getHeight();
            Insets in = getInsets();

            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(1, 1, w - 2, h - 2, 15, 15));

            g2.setColor(combo.getLineColor());
            g2.setStroke(new BasicStroke(2f));
            g2.draw(new RoundRectangle2D.Double(1, 1, w - 2, h - 2, 15, 15));

            int arrowWidth = (arrowButton != null ? arrowButton.getWidth() : 20);
            Rectangle valueBounds = new Rectangle(in.left, in.top, 
                                          w - in.left - in.right - arrowWidth, 
                                          h - in.top - in.bottom);

            if (getSelectedIndex() == -1) {
                g2.setColor(new Color(150, 150, 150));
                g2.setFont(comboBox.getFont()); 
                FontMetrics fm = g2.getFontMetrics();
                int yText = valueBounds.y + (valueBounds.height - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(combo.getLabeText(), valueBounds.x, yText);
            } else {
                paintCurrentValue(g2, valueBounds, false);
            }

            g2.dispose();
        }

        private class ArrowButton extends JButton {
            public ArrowButton() {
                setContentAreaFilled(false);
                setBorder(new EmptyBorder(5, 5, 5, 5));
            }

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), s = 8;
                int x = (w - s) / 2;
                int y = (h - s) / 2;
                int[] px = {x, x + s, x + s / 2};
                int[] py = {y, y, y + s / 2};
                g2.setColor(combo.getArrowColor()); 
                g2.fillPolygon(px, py, 3);
                g2.dispose();
            }
        }
    }
}