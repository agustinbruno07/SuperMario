package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class casaIzquierda extends JPanel implements KeyListener {
    
    private jugador player;
    private Image fondo;
    private Image imagenCofre;
    private Timer gameLoop;
    private JLabel mensajeLabel; 
    private Timer mensajeTimer; 
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private boolean estaEnSalida = false;
    private boolean estaEnCofre = false;
    private boolean cofreAbierto = false;
    private colisionCalle colisiones;

    
    private final int cofreX = 357;
    private final int cofreY = 600;
    private final int cofreAncho = 100;
    private final int cofreAlto = 100;

    public casaIzquierda(JFrame parentFrame) {
        setLayout(null);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        fondo = new ImageIcon("src/resources/images/casa_izquierda.png").getImage();
        
        colisiones = new colisionCalle("src/resources/images/casa_izquierdaColisiones.png");
        	
        if (EstadoJuego.isCofreAbierto()) {
            imagenCofre = new ImageIcon("src/resources/images/cofreAbierto.png").getImage();
            cofreAbierto = true;
        } else {
            imagenCofre = new ImageIcon("src/resources/images/cofre.png").getImage();
        }

        player = new jugador(357, 704);

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

            int oldX = player.getX();
            int oldY = player.getY();

            if (upPressed)    player.moveUp();
            if (downPressed)  player.moveDown();
            if (leftPressed)  player.moveLeft();
            if (rightPressed) player.moveRight();

            // 🔹 Verificar colisiones con la máscara
            if (colisiones.hayColision(player.getBounds(), w, h)) {
                player.setPosition(oldX, oldY);
            }

            player.clampTo(bounds);
            verificarPosicionSalida();
            verificarPosicionCofre();
            repaint();
        });
        gameLoop.start();
    }

    private void verificarPosicionSalida() {
        Rectangle jugadorBounds = player.getBounds();
        
        estaEnSalida = (Math.abs(jugadorBounds.x - 357) <= 20 && 
                       Math.abs(jugadorBounds.y - 704) <= 20);
    }

    private void verificarPosicionCofre() {
        Rectangle jugadorBounds = player.getBounds();
        Rectangle cofreBounds = new Rectangle(cofreX, cofreY, cofreAncho, cofreAlto);
     
        estaEnCofre = jugadorBounds.intersects(cofreBounds);
    }

    private void abrirCofre() {
        if (!cofreAbierto) {
            imagenCofre = new ImageIcon("src/resources/images/cofreAbierto.png").getImage();
            cofreAbierto = true;
            
            EstadoJuego.setCofreAbierto(true);
            
            mostrarMensaje("Encontraste una llave");
            
            repaint();
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

    private void volverACalle(JFrame parentFrame) {
        if (gameLoop != null && gameLoop.isRunning()) gameLoop.stop();
        if (mensajeTimer != null && mensajeTimer.isRunning()) mensajeTimer.stop();
        
        calle callePanel = new calle(0, 286);
        
        parentFrame.getContentPane().removeAll();
        parentFrame.getContentPane().add(callePanel);
        parentFrame.revalidate();
        parentFrame.repaint();
        SwingUtilities.invokeLater(callePanel::requestFocusInWindow);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        g2.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);
        g2.drawImage(imagenCofre, cofreX, cofreY, cofreAncho, cofreAlto, this);
        player.draw(g2);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP)    upPressed = true;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN)  downPressed = true;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT)  leftPressed = true;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) rightPressed = true;
        
        if (key == KeyEvent.VK_S && estaEnSalida) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (parentFrame != null) {
                volverACalle(parentFrame);
            }
        }
        
        if (key == KeyEvent.VK_E && estaEnCofre && !cofreAbierto) {
            abrirCofre();
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