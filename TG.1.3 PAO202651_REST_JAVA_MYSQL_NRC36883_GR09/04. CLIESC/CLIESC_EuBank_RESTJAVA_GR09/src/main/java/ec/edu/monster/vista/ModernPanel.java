package ec.edu.monster.vista;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.beans.BeanProperty;


public class ModernPanel extends JPanel {

    // --- Propiedades personalizables ---
    private Color panelBackground = Color.WHITE;
    private Color borderColor = new Color(200, 200, 200);
    private int borderRadius = 16;
    private int borderThickness = 1;
    private int paddingTop = 5;
    private int paddingLeft = 5;
    private int paddingBottom = 5;
    private int paddingRight = 5;

    // --- Getters y Setters con @BeanProperty ---
    @BeanProperty(preferred = true, description = "Color de fondo del panel")
    public Color getPanelBackground() { return panelBackground; }
    public void setPanelBackground(Color panelBackground) {
        this.panelBackground = panelBackground;
        repaint();
    }

    @BeanProperty(preferred = true, description = "Color del borde")
    public Color getBorderColor() { return borderColor; }
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }

    @BeanProperty(preferred = true, description = "Radio de las esquinas (px)")
    public int getBorderRadius() { return borderRadius; }
    public void setBorderRadius(int borderRadius) {
        this.borderRadius = Math.max(0, borderRadius);
        repaint();
    }

    @BeanProperty(preferred = true, description = "Grosor del borde (px)")
    public int getBorderThickness() { return borderThickness; }
    public void setBorderThickness(int borderThickness) {
        this.borderThickness = Math.max(0, borderThickness);
        repaint();
    }

    @BeanProperty(preferred = true, description = "Padding superior (px)")
    public int getPaddingTop() { return paddingTop; }
    public void setPaddingTop(int paddingTop) {
        this.paddingTop = Math.max(0, paddingTop);
        updatePadding();
    }

    @BeanProperty(preferred = true, description = "Padding izquierdo (px)")
    public int getPaddingLeft() { return paddingLeft; }
    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = Math.max(0, paddingLeft);
        updatePadding();
    }

    @BeanProperty(preferred = true, description = "Padding inferior (px)")
    public int getPaddingBottom() { return paddingBottom; }
    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = Math.max(0, paddingBottom);
        updatePadding();
    }

    @BeanProperty(preferred = true, description = "Padding derecho (px)")
    public int getPaddingRight() { return paddingRight; }
    public void setPaddingRight(int paddingRight) {
        this.paddingRight = Math.max(0, paddingRight);
        updatePadding();
    }

    // --- Constructor ---
    public ModernPanel() {
        setOpaque(false); // Importante: permite pintar fondo personalizado
        updatePadding();
    }

    // --- Actualiza el EmptyBorder con los paddings ---
    private void updatePadding() {
        setBorder(new EmptyBorder(paddingTop, paddingLeft, paddingBottom, paddingRight));
    }

    // --- Pintado personalizado ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Limpia

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = borderRadius * 2;

        // Fondo
        g2.setColor(panelBackground);
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        // Borde
        if (borderThickness > 0 && borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderThickness));
            int offset = borderThickness / 2;
            g2.drawRoundRect(
                offset, offset,
                w - borderThickness, h - borderThickness,
                arc, arc
            );
        }

        g2.dispose();
    }

    // --- Tamaño mínimo recomendado ---
    @Override
    public Dimension getPreferredSize() {
        Dimension pref = super.getPreferredSize();
        Insets padding = getInsets();
        pref.width = Math.max(pref.width, 100 + padding.left + padding.right);
        pref.height = Math.max(pref.height, 20 + padding.top + padding.bottom);
        return pref;
    }
}