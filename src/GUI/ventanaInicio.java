package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;

public class ventanaInicio extends JFrame {
    
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private Image imagenFondo1;

    private static Clip musicaFondo;
   
    public ventanaInicio() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000,700);
        setResizable(false);
        setTitle("Space Invaders");
        imagenFondo1 = new ImageIcon("src/resources/inicio.png").getImage();
        
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagenFondo1 != null) {
                    g.drawImage(imagenFondo1, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JButton btnIniciar = new JButton("Iniciar Juego");
        btnIniciar.setForeground(Color.BLACK);
        btnIniciar.setBackground(Color.LIGHT_GRAY);
        btnIniciar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reproducirSonido("src/resources/sound/BotonPrincipal.wav"); 
                dispose();
                ventanaPrincipal ventana = new ventanaPrincipal();
                ventana.setVisible(true);
            }
        });
        btnIniciar.setBounds(392, 506, 150, 40);
        contentPane.add(btnIniciar);
        
        JButton btnCerrar = new JButton(" Salir");
        btnCerrar.setBackground(Color.RED);
        btnCerrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); 
            }
        });
        btnCerrar.setBounds(392, 557, 150, 40);
        contentPane.add(btnCerrar);

      
    }

   
    public static void iniciarMusicaFondo(String ruta) {
        try {
            if (musicaFondo != null && musicaFondo.isRunning()) {// Detiene la musica si ya esta sonando
                musicaFondo.stop();
                musicaFondo.close();
            }
            File archivo = new File(ruta);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivo);
            musicaFondo = AudioSystem.getClip();
            musicaFondo.open(audioStream);
            musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void reproducirSonido(String ruta) {
        try {
            File archivo = new File(ruta);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivo);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
