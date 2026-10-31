package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class LoginBackgroundPanel extends JPanel {
    private final BufferedImage backgroundImage;

    public LoginBackgroundPanel() {
        setBackground(new Color(11, 31, 58));
        setOpaque(true);
        try {
            backgroundImage = ImageIO.read(
                    LoginBackgroundPanel.class.getResource("/login-background.png"));
        } catch (IOException | IllegalArgumentException ex) {
            throw new IllegalStateException("No se pudo cargar el fondo del inicio de sesión.", ex);
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (backgroundImage == null || getWidth() <= 0 || getHeight() <= 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        double scale = Math.max(
                (double) getWidth() / backgroundImage.getWidth(),
                (double) getHeight() / backgroundImage.getHeight());
        int width = (int) Math.round(backgroundImage.getWidth() * scale);
        int height = (int) Math.round(backgroundImage.getHeight() * scale);
        int x = (getWidth() - width) / 2;
        int y = (getHeight() - height) / 2;
        g2.drawImage(backgroundImage, x, y, width, height, this);
        g2.dispose();
    }
}
