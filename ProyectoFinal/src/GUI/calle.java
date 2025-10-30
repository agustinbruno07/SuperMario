package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class calle extends JPanel implements KeyListener {

    private jugador player;
    private Image fondo;
    private Timer gameLoop;
    private JLabel labelPuerta;
    private JLabel mensajeLabel;
    private Timer mensajeTimer;
    private colisionCalle colisiones; // 🔹 NUEVO

    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private boolean estaEnPuerta = false;
    private boolean estaEnTransicionIzquierda = false; // Para casa izquierda
    private boolean estaEnTransicionDerecha = false;   // NUEVO: Para casa derecha

    // Resolución fija del juego
    private static final int FIXED_WIDTH = 1366;
    private static final int FIXED_HEIGHT = 768;

    public calle() {
        this(641, 692); 
    }

    public calle(int startX, int startY) {
        setLayout(null);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        Musica.detener();

        fondo = new ImageIcon("src/resources/images/puerta.png").getImage();
        player = new jugador(startX, startY);
        
        // 🔹 CARGAR MÁSCARA DE COLISIÓN
        colisiones = new colisionCalle("src/resources/images/colisionesPuerta.png");

        labelPuerta = new JLabel();
        labelPuerta.setBounds(641, 489, 50, 50);
        labelPuerta.setOpaque(false);
        add(labelPuerta);

        mensajeLabel = new JLabel("", JLabel.CENTER);
        mensajeLabel.setBounds(200, 100, 400, 60);
        mensajeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mensajeLabel.setForeground(Color.YELLOW);
        mensajeLabel.setBackground(new Color(0, 0, 0, 180));
        mensajeLabel.setOpaque(true);
        mensajeLabel.setVisible(false);
        add(mensajeLabel);

        mensajeTimer = new Timer(2000, e -> {
            mensajeLabel.setVisible(false);
            ((Timer)e.getSource()).stop();
        });
        mensajeTimer.setRepeats(false);

        addKeyListener(this);
        SwingUtilities.invokeLater(this::requestFocusInWindow);

        gameLoop = new Timer(16, e -> {
            // Trabajamos en coordenadas lógicas (FIXED_WIDTH/FIXED_HEIGHT)
            int w = FIXED_WIDTH;
            int h = FIXED_HEIGHT;
            Rectangle bounds = new Rectangle(0, 0, w, h);

            // Obtener escala actual para adaptar componentes Swing (labels) a fullscreen
            double scaleX = (getWidth() > 0) ? (double) getWidth() / FIXED_WIDTH : 1.0;
            double scaleY = (getHeight() > 0) ? (double) getHeight() / FIXED_HEIGHT : 1.0;

            // Actualizar bounds de los JLabels para que sigan la posición escalada
            int puertaX = (int) Math.round(641 * scaleX);
            int puertaY = (int) Math.round(489 * scaleY);
            int puertaW = Math.max(1, (int) Math.round(50 * scaleX));
            int puertaH = Math.max(1, (int) Math.round(50 * scaleY));
            labelPuerta.setBounds(puertaX, puertaY, puertaW, puertaH);

            int msgW = Math.max(50, (int) Math.round(400 * scaleX));
            int msgX = (int) Math.round((FIXED_WIDTH * scaleX - msgW) / 2.0);
            mensajeLabel.setBounds(msgX, (int) Math.round(100 * scaleY), msgW, (int) Math.round(60 * scaleY));

            // 🔹 GUARDAR POSICIÓN ANTERIOR
            int oldX = player.getX();
            int oldY = player.getY();

            if (upPressed)    player.moveUp();
            if (downPressed)  player.moveDown();
            if (leftPressed)  player.moveLeft();
            if (rightPressed) player.moveRight();
            
            // 🔹 VERIFICAR COLISIÓN usando coordenadas lógicas
            if (colisiones.hayColision(player.getBounds(), FIXED_WIDTH, FIXED_HEIGHT)) {
                // Si hay colisión, volver a la posición anterior
                player.setPosition(oldX, oldY);
            }
            
            player.clampTo(bounds);
            verificarPosicionPuerta();
            verificarPosicionTransicionIzquierda();
            verificarPosicionTransicionDerecha();
            repaint();
        });
        gameLoop.start();
    }

    private void verificarPosicionPuerta() {
        Rectangle jugadorBounds = player.getBounds();
        Rectangle puertaBounds = new Rectangle(641, 489, 50, 50);
        
        estaEnPuerta = jugadorBounds.intersects(puertaBounds);
    }

    private void verificarPosicionTransicionIzquierda() {
        Rectangle jugadorBounds = player.getBounds();
        
        // Para casa izquierda (x=0, y=286)
        estaEnTransicionIzquierda = (jugadorBounds.x <= 0 && Math.abs(jugadorBounds.y - 286) <= 20);
    }

    // NUEVO MÉTODO: Verificar posición para casa derecha
    private void verificarPosicionTransicionDerecha() {
        Rectangle jugadorBounds = player.getBounds();
        
        // Para casa derecha (x=1302, y=267) con margen de ±20 píxeles
        estaEnTransicionDerecha = (jugadorBounds.x >= 1302 && Math.abs(jugadorBounds.y - 267) <= 20);
    }

    private void cambiarACasaIzquierda() {
        if (gameLoop != null && gameLoop.isRunning()) gameLoop.stop();
        if (mensajeTimer != null && mensajeTimer.isRunning()) mensajeTimer.stop();
        
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            casaIzquierda siguientePanel = new casaIzquierda(parentFrame);
            
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(siguientePanel);
            parentFrame.revalidate();
            parentFrame.repaint();
            
            SwingUtilities.invokeLater(siguientePanel::requestFocusInWindow);
        }
    }

    private void cambiarACasaDerecha() {
        if (gameLoop != null && gameLoop.isRunning()) gameLoop.stop();
        if (mensajeTimer != null && mensajeTimer.isRunning()) mensajeTimer.stop();
        
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            casaDerecha siguientePanel = new casaDerecha(parentFrame);
            
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(siguientePanel);
            parentFrame.revalidate();
            parentFrame.repaint();
            
            SwingUtilities.invokeLater(siguientePanel::requestFocusInWindow);
        }
    }

    private void cambiarACasaPrincipal() {
        if (gameLoop != null && gameLoop.isRunning()) gameLoop.stop();
        if (mensajeTimer != null && mensajeTimer.isRunning()) mensajeTimer.stop();
        
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            CasaPrincipal siguientePanel = new CasaPrincipal(parentFrame);
            
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(siguientePanel);
            parentFrame.revalidate();
            parentFrame.repaint();
            
            SwingUtilities.invokeLater(siguientePanel::requestFocusInWindow);
        }
    }

    private void mostrarMensaje(String mensaje) {
        if (!mensajeLabel.isVisible()) {
            mensajeLabel.setText(mensaje);
            mensajeLabel.setVisible(true);
            int x = (FIXED_WIDTH - mensajeLabel.getWidth()) / 2;
            mensajeLabel.setLocation(x, 100);
            mensajeTimer.start();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Escalar el contexto gráfico para que todo se dibuje en coordenadas lógicas
        double sx = (getWidth() > 0) ? (double) getWidth() / FIXED_WIDTH : 1.0;
        double sy = (getHeight() > 0) ? (double) getHeight() / FIXED_HEIGHT : 1.0;

        java.awt.geom.AffineTransform original = g2.getTransform();
        g2.scale(sx, sy);

        // Dibujar fondo y jugador usando coordenadas lógicas
        g2.drawImage(fondo, 0, 0, FIXED_WIDTH, FIXED_HEIGHT, null);
        player.draw(g2);

        // Restaurar transform para que los componentes Swing (labels) no se vean afectados
        g2.setTransform(original);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP)    upPressed = true;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN)  downPressed = true;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT)  leftPressed = true;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) rightPressed = true;
        
        // Verificar si el cofre fue abierto
        if (key == KeyEvent.VK_E && estaEnPuerta) {
            if (EstadoJuego.isCofreAbierto()) {
                cambiarACasaPrincipal();
            } else {
                mostrarMensaje("Parece estar cerrada");
            }
        }
        
        // Para casa izquierda (tecla A)
        if (key == KeyEvent.VK_A && estaEnTransicionIzquierda) {
            cambiarACasaIzquierda();
        }
        
        // NUEVO: Para casa derecha (tecla D)
        if (key == KeyEvent.VK_D && estaEnTransicionDerecha) {
            cambiarACasaDerecha();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP)    upPressed = false;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN)  downPressed = false;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT)  leftPressed = false;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}