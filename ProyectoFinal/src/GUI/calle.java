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
            int w = Math.max(1, getWidth());
            int h = Math.max(1, getHeight());
            Rectangle bounds = new Rectangle(0, 0, w, h);

            // 🔹 GUARDAR POSICIÓN ANTERIOR
            int oldX = player.getX();
            int oldY = player.getY();

            if (upPressed)    player.moveUp();
            if (downPressed)  player.moveDown();
            if (leftPressed)  player.moveLeft();
            if (rightPressed) player.moveRight();
            
            // 🔹 VERIFICAR COLISIÓN
            if (colisiones.hayColision(player.getBounds(), w, h)) {
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
        Rectangle puertaBounds = labelPuerta.getBounds();
        
        estaEnPuerta = jugadorBounds.intersects(puertaBounds);
    }

    private void verificarPosicionTransicionIzquierda() {
        Rectangle jugadorBounds = player.getBounds();
        
        // Para casa izquierda (x=0, y=286)
        estaEnTransicionIzquierda = (jugadorBounds.x <= 0 && Math.abs(jugadorBounds.y - 286) <= 20);
    }

    // NUEVO MÃ‰TODO: Verificar posiciÃ³n para casa derecha
    private void verificarPosicionTransicionDerecha() {
        Rectangle jugadorBounds = player.getBounds();
        
        // Para casa derecha (x=1302, y=267) con margen de Â±20 pÃ­xeles
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
            int x = (getWidth() - mensajeLabel.getWidth()) / 2;
            mensajeLabel.setLocation(x, 100);
            mensajeTimer.start();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
        player.draw(g2);
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