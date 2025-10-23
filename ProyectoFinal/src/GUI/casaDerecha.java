package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class casaDerecha extends JPanel implements KeyListener {
    
    private jugador player;
    private Image fondo;
    private Timer gameLoop;
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private boolean estaEnSalida = false; 

    public casaDerecha(JFrame parentFrame) {
        setLayout(null);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        fondo = new ImageIcon("src/resources/images/casas_derecha.png").getImage();

        player = new jugador(925, 699);

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
            
            verificarPosicionSalida();
            
            repaint();
        });
        gameLoop.start();
    }

    private void verificarPosicionSalida() {
        Rectangle jugadorBounds = player.getBounds();
        
        estaEnSalida = (Math.abs(jugadorBounds.x - 925) <= 20 && 
                       Math.abs(jugadorBounds.y - 699) <= 20);
    }

    private void volverACalle(JFrame parentFrame) {
        if (gameLoop != null && gameLoop.isRunning()) gameLoop.stop();
        
        calle callePanel = new calle(1302, 267);
        
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