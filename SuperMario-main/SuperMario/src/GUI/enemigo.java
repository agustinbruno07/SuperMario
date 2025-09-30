package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class enemigo {
    public static final int WIDTH = 60; // Igual que el jugador
    public static final int HEIGHT = 60; // Igual que el jugador
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

    private int vidas = 1; // Por defecto, 1 vida
    private boolean esJefe = false;

    // --- DASH para jefe LVL3 ---
    private boolean enDash = false;
    private int dashFrames = 0;
    private int dashCooldown = 0;
    private static final int DASH_DIST = 60; // pixeles por dash
    private static final int DASH_DURATION = 6; // frames
    private static final int DASH_COOLDOWN_MAX = 90; // frames

    // --- DASH para jefe LVL4 ---
    private int dashsSeguidos = 0; // Para jefe LVL4
    private static final int MAX_DASHS_LVL4 = 3;
    private int dashsRestantes = 0;
    private boolean esJefeFinal = false; // Para distinguir jefe LVL4

    public enemigo(int worldX, int startY, ImageIcon rightIcon, ImageIcon leftIcon) {
        this(worldX, startY, rightIcon, leftIcon, 1, false);
    }
    // Constructor para jefe con más vidas
    public enemigo(int worldX, int startY, ImageIcon rightIcon, ImageIcon leftIcon, int vidas, boolean esJefe) {
        this.worldX = worldX;
        this.y = startY;
        this.vidas = vidas;
        this.esJefe = esJefe;
        // Escalar las imágenes al tamaño del sprite
        ImageIcon scaledRight = null;
        ImageIcon scaledLeft = null;
        if (rightIcon != null && rightIcon.getIconWidth() > 0 && rightIcon.getIconHeight() > 0) {
            Image img = rightIcon.getImage().getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
            scaledRight = new ImageIcon(img);
        }
        if (leftIcon != null && leftIcon.getIconWidth() > 0 && leftIcon.getIconHeight() > 0) {
            Image img = leftIcon.getImage().getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
            scaledLeft = new ImageIcon(img);
        }
        this.rightIcon = scaledRight != null ? scaledRight : rightIcon;
        this.leftIcon = scaledLeft != null ? scaledLeft : leftIcon;
        this.sprite = new JLabel(this.rightIcon != null ? this.rightIcon : new ImageIcon());
        this.sprite.setBounds(0, 0, WIDTH, HEIGHT);
    }
    // Constructor para jefe con más vidas y tipo de jefe final
    public enemigo(int worldX, int startY, ImageIcon rightIcon, ImageIcon leftIcon, int vidas, boolean esJefe, boolean esJefeFinal) {
        this(worldX, startY, rightIcon, leftIcon, vidas, esJefe);
        this.esJefeFinal = esJefeFinal;
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
    public void updateHorizontal(List<Rectangle> tuberias, int mapLimit, int playerX) {
        int limiteIzq = 0;
        int limiteDer = mapLimit - player.WIDTH;
        // Ajuste: jefe final no puede pasar el límite visible del jugador
        if (esJefeFinal) {
            // El jefe no puede ir más allá del área accesible para el jugador
            // (el mismo límite que usa ventanaPrincipal para el jugador)
            // panelW no está disponible aquí, así que se debe pasar como argumento si se quiere máxima precisión.
            // Como solución, restamos un margen extra (por ejemplo, 1000) para evitar que el jefe quede fuera de pantalla.
            limiteDer = mapLimit - 1000 - WIDTH;
        }
        // Jefe LVL4: 8 vidas, puede hacer hasta 3 dashes seguidos
        if (esJefeFinal && vidas == 8) {
            if (!enDash && dashCooldown == 0 && dashsRestantes == 0 && Math.random() < 0.03) {
                dashsRestantes = MAX_DASHS_LVL4;
            }
            if (!enDash && dashCooldown == 0 && dashsRestantes > 0) {
                if (playerX > worldX) dir = -1; else dir = 1;
                enDash = true;
                dashFrames = DASH_DURATION;
                dashsSeguidos++;
                dashsRestantes--;
            }
            if (enDash) {
                int nextX = worldX + dir * (DASH_DIST / DASH_DURATION);
                if (nextX < limiteIzq) nextX = limiteIzq;
                if (nextX > limiteDer) nextX = limiteDer;
                worldX = nextX;
                dashFrames--;
                if (dashFrames <= 0) {
                    enDash = false;
                    if (dashsRestantes > 0) {
                        dashCooldown = 10; // Pequeño cooldown entre dashes seguidos
                    } else {
                        dashCooldown = DASH_COOLDOWN_MAX;
                        dashsSeguidos = 0;
                    }
                }
                return;
            } else if (dashCooldown > 0) {
                dashCooldown--;
            }
        }
        // Solo jefe LVL3 (5 vidas) puede hacer dash
        if (esJefe && vidas == 5) {
            if (!enDash && dashCooldown == 0 && Math.random() < 0.02) { // 2% chance cada frame
                // Decide dirección para esquivar al jugador
                if (playerX > worldX) dir = -1; else dir = 1;
                enDash = true;
                dashFrames = DASH_DURATION;
            }
            if (enDash) {
                int nextX = worldX + dir * (DASH_DIST / DASH_DURATION);
                // Chequeo límites igual que el jugador
                if (nextX < limiteIzq) nextX = limiteIzq;
                if (nextX > limiteDer) nextX = limiteDer;
                worldX = nextX;
                dashFrames--;
                if (dashFrames <= 0) {
                    enDash = false;
                    dashCooldown = DASH_COOLDOWN_MAX;
                }
                return; // No movimiento normal durante dash
            } else if (dashCooldown > 0) {
                dashCooldown--;
            }
        }
        // Propuesta de movimiento
        int nextX = worldX + dir * SPEED;
        if (nextX < 0) {
            nextX = 0; dir = +1;
        }
        if (nextX > limiteDer) {
            nextX = limiteDer; dir = -1;
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

    public boolean esJefe() { return esJefe; }
    public boolean esJefeFinal() { return esJefeFinal; }
    public int getVidas() { return vidas; }
    public void restarVida() { vidas--; }
    public void cambiarDireccion() {
        dir = -dir;
    }
}