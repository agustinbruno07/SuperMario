package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class enemigo {
    public static final int WIDTH = 48;
    public static final int HEIGHT = 64;
    // Físicas simples
    private static final int SPEED = 3;
    private static final double GRAVITY = 1.0;
    private static final double MAX_FALL = 16.0;

    // Estado
    private int worldX;       // posición en coordenadas de “mundo”
    private int y;            // posición vertical en pantalla (no depende del offset)
    private double velY = 0;  // velocidad vertical
    private int dir = -1;     // -1 izquierda, +1 derecha
    private boolean enAire = false;

    // Sprites
    private final ImageIcon rightIcon;
    private final ImageIcon leftIcon;
    private final JLabel sprite;

    public enemigo(int worldX, int startY, ImageIcon rightIcon, ImageIcon leftIcon) {
        this.worldX = worldX;
        this.y = startY;
        this.rightIcon = rightIcon;
        this.leftIcon = leftIcon;

        this.sprite = new JLabel(rightIcon != null ? rightIcon : new ImageIcon());
        this.sprite.setBounds(0, 0, WIDTH, HEIGHT);
    }

    public JLabel getLabel() {
        return sprite;
    }

    public int getWorldX() {
        return worldX;
    }

    public int getY() {
        return y;
    }

    public Rectangle getWorldRect() {
        return new Rectangle(worldX, y, WIDTH, HEIGHT);
    }

    /** Llama cada frame para mover en X y rebotar ante tuberías o bordes del mapa */
    public void updateHorizontal(List<Rectangle> tuberias, int mapLimit) {
        // Propuesta de movimiento
        int nextX = worldX + dir * SPEED;

        // Choque con límites del mundo
        if (nextX < 0) {
            nextX = 0; dir = +1;
        }
        if (nextX > mapLimit - WIDTH) {
            nextX = mapLimit - WIDTH; dir = -1;
        }

        // Chequeo contra tuberías (paredes)
        Rectangle futuro = new Rectangle(nextX, y, WIDTH, HEIGHT);
        Rectangle col = null;
        for (Rectangle t : tuberias) {
            if (futuro.intersects(t)) {
                col = t; break;
            }
        }
        if (col == null) {
            worldX = nextX;
        } else {
            // Al chocar, nos pegamos al borde y damos vuelta
            if (dir > 0) { // viniendo desde la izquierda
                worldX = col.x - WIDTH;
            } else {       // viniendo desde la derecha
                worldX = col.x + col.width;
            }
            dir = -dir;
        }
    }

    /** Aplica gravedad y apoya en el y “suelo” recibido */
    public void updateVertical(int groundY) {
        // ¿Está por encima del “suelo” virtual? -> cae
        if (y < groundY) {
            velY += GRAVITY;
            if (velY > MAX_FALL) velY = MAX_FALL;
            y += (int)Math.round(velY);
            enAire = true;
            if (y >= groundY) {
                y = groundY;
                velY = 0;
                enAire = false;
            }
        } else {
            // En suelo/plataforma
            y = groundY;
            velY = 0;
            enAire = false;
        }
    }

    /** Actualiza icono y posición del JLabel en pantalla usando el offset de cámara */
    public void syncSprite(int mapOffset) {
        int screenX = worldX - mapOffset;
        sprite.setBounds(screenX, y, WIDTH, HEIGHT);
        sprite.setIcon(dir >= 0 ? rightIcon : leftIcon);
    }
}