package GUI;
import javax.swing.JPanel;
import GUI.Musica;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

public class ventanaInicio extends JPanel {
    private Image imagenFondo;
    private JFrame parentFrame;
    // Resolución fija del juego
    private static final int FIXED_WIDTH = 1366;
    private static final int FIXED_HEIGHT = 768;
    
    public ventanaInicio(JFrame frame) {
        this.parentFrame = frame;
        setLayout(null);
        setFocusable(true);
        imagenFondo = null;
        Musica.reproducir("/resources/sonidos/sonidoInicio.wav");

        // Forzar tamaño fijo
        setPreferredSize(new Dimension(FIXED_WIDTH, FIXED_HEIGHT));

        // Construir UI para la resolución fija
        createOrUpdateUI(FIXED_WIDTH, FIXED_HEIGHT);
    }

    // Permite actualizar la referencia al frame padre si re-creamos el JFrame (fallback fullscreen)
    public void setParentFrame(JFrame frame) {
        this.parentFrame = frame;
    }

    private void createOrUpdateUI(int width, int height) {
        removeAll();
        // Establecer tamaño preferido
        if (width > 0 && height > 0) setPreferredSize(new Dimension(width, height));

        // Usar tamaños fijos pensados para 1366x768
        final int buttonWidth = 407;
        final int buttonHeight = 46;
        final int startX = (width - buttonWidth) / 2;
        final int startY = (int) Math.round(height * 0.45);

        // Cargar iconos y fondo (misma lógica que antes)
        ImageIcon iconConfig = new ImageIcon();
        ImageIcon iconIniciar = new ImageIcon();
        ImageIcon iconSalir = new ImageIcon();
        ImageIcon iconRanking = new ImageIcon();
        try {
            if (getClass().getResource("/resources/images/fondo.png") != null) {
                imagenFondo = new ImageIcon(getClass().getResource("/resources/images/fondo.png")).getImage();
            } else {
                imagenFondo = new ImageIcon("src/resources/images/fondo.png").getImage();
            }
        } catch (Exception ex) {
            imagenFondo = null;
        }

        try {
            if (getClass().getResource("/resources/images/config.png") != null) {
                iconConfig = new ImageIcon(getClass().getResource("/resources/images/config.png"));
            } else {
                iconConfig = new ImageIcon("src/resources/images/config.png");
            }
            if (getClass().getResource("/resources/images/inicio.png") != null) {
                iconIniciar = new ImageIcon(getClass().getResource("/resources/images/inicio.png"));
            } else {
                iconIniciar = new ImageIcon("src/resources/images/inicio.png");
            }
            if (getClass().getResource("/resources/images/salir.png") != null) {
                iconSalir = new ImageIcon(getClass().getResource("/resources/images/salir.png"));
            } else {
                iconSalir = new ImageIcon("src/resources/images/salir.png");
            }
            if (getClass().getResource("/resources/images/ranking.png") != null) {
                iconRanking = new ImageIcon(getClass().getResource("/resources/images/ranking.png"));
            } else {
                iconRanking = new ImageIcon("src/resources/images/ranking.png");
            }
        } catch (Exception ex) {
            // deja iconos vacíos
        }

        // Escalar iconos al tamaño de los botones
        try {
            if (iconIniciar.getImage() != null) {
                Image img = iconIniciar.getImage().getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
                iconIniciar = new ImageIcon(img);
            }
            if (iconConfig.getImage() != null) {
                Image img = iconConfig.getImage().getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
                iconConfig = new ImageIcon(img);
            }
            if (iconRanking.getImage() != null) {
                Image img = iconRanking.getImage().getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
                iconRanking = new ImageIcon(img);
            }
            if (iconSalir.getImage() != null) {
                Image img = iconSalir.getImage().getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
                iconSalir = new ImageIcon(img);
            }
        } catch (Exception e) {
            // Si falla el escalado, dejamos los iconos originales
        }

        JButton btnIniciar = new JButton();
        btnIniciar.setBounds(startX, startY, buttonWidth, buttonHeight);
        btnIniciar.setIcon(iconIniciar);
        btnIniciar.setBorderPainted(false);
        btnIniciar.setContentAreaFilled(false);
        btnIniciar.setFocusPainted(false);
        btnIniciar.setOpaque(false);
        add(btnIniciar);

        btnIniciar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Musica.reproducir("/resources/sonidos/Inicio.wav");
                parentFrame.getContentPane().removeAll();
                parentFrame.getContentPane().add(new dialogo1(parentFrame)); 
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });

        JButton btnConfig = new JButton();
        btnConfig.setBounds(startX, startY + buttonHeight + 10, buttonWidth, buttonHeight);
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

        JButton btnRanking = new JButton();
        btnRanking.setBounds(startX, startY + 2*(buttonHeight + 10), buttonWidth, buttonHeight);
        btnRanking.setIcon(iconRanking);
        btnRanking.setBorderPainted(false);
        btnRanking.setContentAreaFilled(false);
        btnRanking.setFocusPainted(false);
        btnRanking.setOpaque(false);
        add(btnRanking);

        btnRanking.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentFrame.getContentPane().removeAll();
                parentFrame.getContentPane().add(new Ranking(parentFrame));
                parentFrame.revalidate();
                parentFrame.repaint();
            }
        });

        JButton btnSalir = new JButton();
        btnSalir.setBounds(startX, startY + 3*(buttonHeight + 10), buttonWidth, buttonHeight);
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

        setFocusable(true);
        revalidate();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) {
            // Dibujar usando el tamaño actual del panel para que funcione en fullscreen
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
    
    public static void mostrarVentana() {
         // Usar una referencia final mutable para el JFrame para permitir uso dentro de lambdas
         final JFrame[] frameRef = new JFrame[1];
         JFrame initial = new JFrame("Proyecto Final");
         initial.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         // Intentar abrir en pantalla completa borderless por defecto
         GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         GraphicsDevice gd = ge.getDefaultScreenDevice();

         initial.setUndecorated(true);
         ventanaInicio panel = new ventanaInicio(initial);
         initial.getContentPane().add(panel);
         initial.setResizable(false);
         initial.setVisible(true);

         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         // Actualizar resolución en el manejador de escala para que otros paneles puedan adaptarse
         ManejadorEscala.setResolution(screenSize.width, screenSize.height);

         frameRef[0] = initial;

         if (gd != null && gd.isFullScreenSupported()) {
             try {
                 gd.setFullScreenWindow(frameRef[0]);
             } catch (Exception ex) {
                 // fallback: maximizar ventana
                 // intentar recrear como borderless que ocupa toda la pantalla (incluye area de taskbar)
                 frameRef[0].dispose();
                 frameRef[0] = new JFrame("Proyecto Final");
                 frameRef[0].setUndecorated(true);
                 frameRef[0].setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                 frameRef[0].getContentPane().add(panel);
                 panel.setParentFrame(frameRef[0]);
                 frameRef[0].setBounds(0, 0, screenSize.width, screenSize.height);
                 frameRef[0].setVisible(true);
                 // Forzar al frente temporalmente y luego desactivar alwaysOnTop (para no interferir con Alt-Tab)
                 try { frameRef[0].setAlwaysOnTop(true); } catch (Exception ignore) {}
                 try { frameRef[0].toFront(); } catch (Exception ignore) {}
                 try {
                     javax.swing.Timer t = new javax.swing.Timer(200, ev -> {
                         try { frameRef[0].setAlwaysOnTop(false); } catch (Exception ignore) {}
                         ((javax.swing.Timer)ev.getSource()).stop();
                     });
                     t.setRepeats(false);
                     t.start();
                 } catch (Exception ignore) {}
             }
         } else {
             // Si no hay soporte exclusivo, recrear borderless y cubrir toda la pantalla
             frameRef[0].dispose();
             frameRef[0] = new JFrame("Proyecto Final");
             frameRef[0].setUndecorated(true);
             frameRef[0].setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
             frameRef[0].getContentPane().add(panel);
             panel.setParentFrame(frameRef[0]);
             frameRef[0].setBounds(0, 0, screenSize.width, screenSize.height);
             frameRef[0].setVisible(true);
             // Forzar al frente temporalmente y luego desactivar alwaysOnTop (para no interferir con Alt-Tab)
             try { frameRef[0].setAlwaysOnTop(true); } catch (Exception ignore) {}
             try { frameRef[0].toFront(); } catch (Exception ignore) {}
             try {
                 javax.swing.Timer t2 = new javax.swing.Timer(200, ev -> {
                     try { frameRef[0].setAlwaysOnTop(false); } catch (Exception ignore) {}
                     ((javax.swing.Timer)ev.getSource()).stop();
                 });
                 t2.setRepeats(false);
                 t2.start();
             } catch (Exception ignore) {}
         }

         frameRef[0].requestFocus();
      }
  }