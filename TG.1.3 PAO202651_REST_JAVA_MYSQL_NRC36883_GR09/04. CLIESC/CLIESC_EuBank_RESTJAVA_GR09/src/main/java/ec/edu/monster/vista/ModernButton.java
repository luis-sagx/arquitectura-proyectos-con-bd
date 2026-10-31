package ec.edu.monster.vista;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.beans.BeanProperty;

public class ModernButton extends JButton {

    // --- Propiedades Personalizables ---
    private Color backgroundColor = new Color(11, 31, 58); // Fondo por defecto
    private Color hoverColor = new Color(22, 58, 95);     // Fondo al hover
    private Color pressedColor = new Color(7, 20, 38);    // Fondo al presionar
    private Color foregroundColor = Color.WHITE;           // Color del texto
    private Color borderColor = backgroundColor;           // Color del borde
    private int borderRadius = 15;                         // Radio del borde
    private int borderThickness = 2;                       // Grosor del borde

    private boolean isHovered = false;
    private boolean isPressed = false;

    // --- Getters y Setters con @BeanProperty para el diseñador visual ---
    @BeanProperty(preferred = true, description = "Color de fondo del botón")
    public Color getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaint();
    }

    @BeanProperty(preferred = true, description = "Color al pasar el mouse")
    public Color getHoverColor() { return hoverColor; }
    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
        repaint();
    }

    @BeanProperty(preferred = true, description = "Color al presionar")
    public Color getPressedColor() { return pressedColor; }
    public void setPressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
        repaint();
    }

    @BeanProperty(preferred = true, description = "Color del texto")
    public Color getForegroundColor() { return foregroundColor; }
    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
        setForeground(foregroundColor);
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
        this.borderRadius = borderRadius;
        repaint();
    }

    @BeanProperty(preferred = true, description = "Grosor del borde (px)")
    public int getBorderThickness() { return borderThickness; }
    public void setBorderThickness(int borderThickness) {
        this.borderThickness = Math.max(1, borderThickness);
        repaint();
    }

    // --- Constructor ---
    public ModernButton() {
        this("");
    }

    public ModernButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(new EmptyBorder(10, 20, 10, 20));
        setForeground(foregroundColor);
        setFont(getFont().deriveFont(Font.BOLD));

        // --- Efectos de mouse ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    // --- Sobrescribir el tamaño mínimo para que no se vea raro ---
    @Override
    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        dim.height = Math.max(dim.height, 40);
        return dim;
    }

    // --- Pintado personalizado ---
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth(), h = getHeight();

        // Determinar color actual
        Color currentBg = backgroundColor;
        if (isPressed && isHovered) {
            currentBg = pressedColor;
        } else if (isHovered) {
            currentBg = hoverColor;
        }

        // Fondo redondeado
        g2.setColor(currentBg);
        g2.fill(new RoundRectangle2D.Double(0, 0, w, h, borderRadius, borderRadius));

        // Borde
        if (borderThickness > 0) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderThickness));
            g2.draw(new RoundRectangle2D.Double(
                borderThickness / 2.0, borderThickness / 2.0,
                w - borderThickness, h - borderThickness,
                borderRadius, borderRadius
            ));
        }

        g2.dispose();

        // Pintar el texto (usamos el paint de JButton pero con nuestro fondo ya dibujado)
        super.paintComponent(g);
    }

    // --- Opcional: sombra suave (descomenta si quieres) ---
    /*
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int shadowSize = 6;
        Color shadowColor = new Color(0, 0, 0, 40);

        // Sombra
        g2.setColor(shadowColor);
        g2.fill(new RoundRectangle2D.Double(
            shadowSize, shadowSize, w - shadowSize * 2, h - shadowSize * 2,
            borderRadius, borderRadius
        ));

        g2.dispose();
        super.paintComponent(g); // Llama al fondo normal
    }
    */
}