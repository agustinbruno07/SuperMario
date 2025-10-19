package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CasaPrincipal extends JPanel implements KeyListener {
    
    private jugador player;
    private Image fondo;
    private Timer gameLoop;
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    public CasaPrincipal(JFrame parentFrame) {
        setLayout(null);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        fondo = new ImageIcon("src/resources/images/casaPrincipal.png").getImage();

        player = new jugador(650, 699);

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
            System.out.println(player.getX()+"," + player.getY());
            player.clampTo(bounds);
            
            repaint();
        });
        gameLoop.start();
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