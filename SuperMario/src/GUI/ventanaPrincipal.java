package GUI;

import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ventanaPrincipal extends JFrame implements KeyListener {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    public boolean  leftPressed, rightPressed;
    private player jugador;
    private JPanel spriteJugador; 
    private Timer gameTimer;

    public ventanaPrincipal() {
    	setSize(1000,700);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contentPane = new JPanel();
         
        contentPane.setBackground(Color.BLACK);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);
        
  
        
        jugador = new player(470,569);
   
        spriteJugador = new JPanel();
        spriteJugador.setBackground(new Color(0x33FF66));
        spriteJugador.setBounds(jugador.getX(), jugador.getY(), jugador.getWidth(), jugador.getHeight());
        contentPane.add(spriteJugador);
        
       
        
        gameTimer = new Timer(8, e -> { 
          repaint();
           update();
        });
        gameTimer.start();

        addKeyListener(this);
        setFocusable(true);
    }
    
    
  public void update() {
	  if (leftPressed) {
          jugador.moveLeft();
      }
      if (rightPressed) {
          jugador.moveRight();        
      }
      
  }
  
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) rightPressed = true;
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}