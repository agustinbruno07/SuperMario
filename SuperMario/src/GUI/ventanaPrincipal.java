package GUI;

import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ventanaPrincipal extends JFrame implements KeyListener {
    private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    private player jugador;
    private JPanel Mario;

    private Timer gameTimer;
    private boolean leftPressed, rightPressed;

    private Image imgSuelo;
    private int worldOffset = 0;   // desplazamiento global del mundo
    private final int GROUND_H = 160; 

    public ventanaPrincipal() {
        setSize(1000, 700);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                // dibujar suelo infinito
                int y = getHeight() - GROUND_H;
                drawGround(g2, imgSuelo, y, GROUND_H, worldOffset);

                g2.dispose();
            }
        };
        contentPane.setBackground(Color.BLACK);
        contentPane.setBorder(new EmptyBorder(5,5,5,5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // cargar suelo
        imgSuelo = new ImageIcon("src/resources/suelo.png").getImage();

        // jugador lógico
        jugador = new player(470, 420);

        // jugador visible
        Mario = new JPanel();
        Mario.setBackground(new Color(0x33FF66));
        Mario.setBounds(465, 405, jugador.getWidth(), jugador.getHeight());
        contentPane.add(Mario);

        // bucle
        gameTimer = new Timer(16, e -> {
            update();
            Mario.setBounds(jugador.getX(), jugador.getY(), jugador.getWidth(), jugador.getHeight());
            contentPane.repaint();
            jugador.update();
        });
        gameTimer.start();

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    private void update() {
        if (leftPressed) {
            jugador.moveLeft();
            worldOffset -= 4; // mover mundo a la derecha
        }
        if (rightPressed) {
            jugador.moveRight();
            worldOffset += 4; // mover mundo a la izquierda
        }
        
    }

    private void drawGround(Graphics2D g2, Image tile, int posY, int height, int offset) {
        if (tile == null) return;

        int tileW = tile.getWidth(null);
        if (tileW <= 0) return;

        // wrap horizontal usando worldOffset
        int xOffset = -(offset % tileW);
        if (xOffset > 0) xOffset -= tileW;

        for (int x = xOffset; x < contentPane.getWidth(); x += tileW) {
            g2.drawImage(tile, x, posY, tileW, height, null);
        }
    }

    
    
    // teclado
    @Override public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_LEFT  || k == KeyEvent.VK_A) leftPressed  = true;
        if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) rightPressed = true;
        if (k == KeyEvent.VK_SPACE) jugador.jump();
    }
    @Override public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_LEFT  || k == KeyEvent.VK_A) leftPressed  = false;
        if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) rightPressed = false;
    }
    @Override public void keyTyped(KeyEvent e) {}
}
