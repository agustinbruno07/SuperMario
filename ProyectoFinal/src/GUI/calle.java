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

    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private boolean estaEnPuerta = false;
    private boolean estaEnTransicion = false;

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

            if (upPressed)    player.moveUp();
            if (downPressed)  player.moveDown();
            if (leftPressed)  player.moveLeft();
            if (rightPressed) player.moveRight();
            
            player.clampTo(bounds);
            verificarPosicionPuerta();
            verificarPosicionTransicion();
            repaint();
        });
        gameLoop.start();
    }

    private void verificarPosicionPuerta() {
        Rectangle jugadorBounds = player.getBounds();
        Rectangle puertaBounds = labelPuerta.getBounds();
        
        estaEnPuerta = jugadorBounds.intersects(puertaBounds);
    }

    private void verificarPosicionTransicion() {
        Rectangle jugadorBounds = player.getBounds();
        
        estaEnTransicion = (jugadorBounds.x <= 0 && Math.abs(jugadorBounds.y - 286) <= 20);
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
        
        if (key == KeyEvent.VK_E && estaEnPuerta) {
            if (EstadoJuego.isCofreAbierto()) {
                cambiarACasaPrincipal();
            } else {
                mostrarMensaje("Parece estar cerrada");
            }
        }
        
        if (key == KeyEvent.VK_A && estaEnTransicion) {
            cambiarACasaIzquierda();
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