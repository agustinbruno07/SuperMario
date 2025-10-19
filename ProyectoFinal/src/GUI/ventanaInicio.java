package GUI;
import javax.swing.JPanel;

import GUI.Musica;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class ventanaInicio extends JPanel {
    private Image imagenFondo;
    private JFrame parentFrame;
    
    public ventanaInicio(JFrame frame) {
        this.parentFrame = frame;
        setLayout(null);
        imagenFondo = new ImageIcon("src/resources/images/fondo.png").getImage();
        Musica.reproducir("src/resources/sonidos/sonidoInicio.wav");
        
        ImageIcon iconConfig = new ImageIcon("src/resources/images/config.png");
        ImageIcon iconIniciar = new ImageIcon("src/resources/images/inicio.png");
        ImageIcon iconSalir = new ImageIcon("src/resources/images/salir.png");
        ImageIcon iconRanking = new ImageIcon("src/resources/images/ranking.png");
      
        JButton btnIniciar = new JButton();
        btnIniciar.setBounds(420, 450, 407, 46);
        btnIniciar.setIcon(iconIniciar);
        btnIniciar.setBorderPainted(false);
        btnIniciar.setContentAreaFilled(false);
        btnIniciar.setFocusPainted(false);
        btnIniciar.setOpaque(false);
        add(btnIniciar);
        
        btnIniciar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Musica.reproducir("src/resources/sonidos/Inicio.wav");
                parentFrame.getContentPane().removeAll();
                parentFrame.getContentPane().add(new dialogo1(parentFrame)); 
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
        
        JButton btnConfig = new JButton();
        btnConfig.setBounds(420, 510, 407, 50); 
        btnConfig.setIcon(iconConfig);
        btnConfig.setBorderPainted(false);
        btnConfig.setContentAreaFilled(false);
        btnConfig.setFocusPainted(false);
        btnConfig.setOpaque(false);
        add(btnConfig);
        
        btnConfig.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                config.mostrarVentanaConfig(parentFrame);
            }
        });
        
        // NUEVO BOTÓN: Ranking
        JButton btnRanking = new JButton();
        btnRanking.setBounds(420, 570, 407, 50); 
        btnRanking.setIcon(iconRanking);
        btnRanking.setBorderPainted(false);
        btnRanking.setContentAreaFilled(false);
        btnRanking.setFocusPainted(false);
        btnRanking.setOpaque(false);
        add(btnRanking);
        
        btnRanking.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentFrame.getContentPane().removeAll();
                parentFrame.getContentPane().add(new Ranking());
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });
        
        
        JButton btnSalir = new JButton();
        btnSalir.setBounds(420, 630, 407, 50); 
        btnSalir.setIcon(iconSalir);
        btnSalir.setBorderPainted(false);
        btnSalir.setContentAreaFilled(false);
        btnSalir.setFocusPainted(false);
        btnSalir.setOpaque(false);
        add(btnSalir);
        
        btnSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
    
    public static void mostrarVentana() {
        JFrame frame = new JFrame("Proyecto Final");
        frame.setUndecorated(true); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        
        frame.getContentPane().add(new ventanaInicio(frame));
        
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(frame);
        } else {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        }
    }
}