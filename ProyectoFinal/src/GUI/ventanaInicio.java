package GUI;
import javax.swing.JPanel;
import GUI.Musica;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Dimension;
import java.awt.Toolkit;

public class ventanaInicio extends JPanel {
    private Image imagenFondo;
    private JFrame parentFrame;
    
    public ventanaInicio(JFrame frame) {
        this.parentFrame = frame;
        setLayout(null);
        imagenFondo = null;
        Musica.reproducir("/resources/sonidos/sonidoInicio.wav");

        // Construir UI inicial según el tamaño actual (o resolución configurada si aún no visible)
        Dimension parentSize = parentFrame.getSize();
        int width = parentSize.width <= 0 ? config.getResolucionAncho() : parentSize.width;
        int height = parentSize.height <= 0 ? config.getResolucionAlto() : parentSize.height;
        createOrUpdateUI(width, height);

        // Reconstruir y reescalar cuando el panel cambie de tamaño (por ejemplo al entrar en fullscreen)
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension d = getSize();
                createOrUpdateUI(d.width, d.height);
            }
        });
    }

    private void createOrUpdateUI(int width, int height) {
        removeAll();
        // Establecer tamaño preferido si es necesario
        if (width > 0 && height > 0) setPreferredSize(new Dimension(width, height));

        // Escalado relativo a una resolución base (para que la UI mantenga proporciones)
        final int BASE_W = 1920;
        final int BASE_H = 1080;
        double scale = Math.min((double) width / BASE_W, (double) height / BASE_H);
        if (scale <= 0) scale = 1.0;
        int buttonWidth = Math.max(120, (int) Math.round(407 * scale));
        int buttonHeight = Math.max(32, (int) Math.round(46 * scale));
        int startX = (width - buttonWidth) / 2;
        int startY = (int) Math.round(height * 0.45);

        // Cargar iconos y fondo
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

        // Escalar íconos al tama�o de los botones para mantener proporciones en distintas resoluciones
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
        btnConfig.setBounds(startX, startY + buttonHeight + (int)(10 * scale), buttonWidth, buttonHeight);
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
        btnRanking.setBounds(startX, startY + 2*(buttonHeight + (int)(10 * scale)), buttonWidth, buttonHeight);
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
        btnSalir.setBounds(startX, startY + 3*(buttonHeight + (int)(10 * scale)), buttonWidth, buttonHeight);
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
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
    
    public static void mostrarVentana() {
         JFrame frame = new JFrame("Proyecto Final");
        // Usar undecorated solo si la configuraci�n lo solicita
        boolean pantalla = config.isPantallaCompleta();
        frame.setUndecorated(pantalla);
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
         GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         GraphicsDevice gd = ge.getDefaultScreenDevice();
         
         // Obtener la resoluci�n configurada
         int width = config.getResolucionAncho();
         int height = config.getResolucionAlto();
         
         // Establecer el tama�o del frame
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         if (pantalla) {
            // En pantalla completa usamos el tama�o de pantalla
            width = screenSize.width;
            height = screenSize.height;
            frame.setSize(width, height);
            frame.getContentPane().add(new ventanaInicio(frame));
            if (gd.isFullScreenSupported()) {
                gd.setFullScreenWindow(frame);
                // Intentar ajustar la DisplayMode a la resolución seleccionada
                boolean ok = config.applyDisplayModeIfNeeded(gd);
                if (!ok) {
                    System.out.println("[ventanaInicio] applyDisplayMode failed at startup, using borderless fallback");
                    // recreate borderless fullscreen
                    frame.dispose();
                    JFrame fb = new JFrame("Proyecto Final");
                    fb.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    fb.setUndecorated(true);
                    fb.setResizable(false);
                    fb.getContentPane().add(new ventanaInicio(fb));
                    fb.setBounds(0, 0, screenSize.width, screenSize.height);
                    fb.setVisible(true);
                    fb.requestFocus();
                    return;
                }
            } else {
                // Si no soporta full screen, decorar como no decorado y maximizar
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setVisible(true);
            }
        } else {
             // En ventana normal limitar la resoluci�n al tama�o de pantalla
             width = Math.min(width, screenSize.width);
             height = Math.min(height, screenSize.height);
            frame.setUndecorated(false);
            frame.setResizable(true);
            ventanaInicio panel = new ventanaInicio(frame);
            // Asegurar que el panel tenga la resolución deseada como preferredSize
            panel.setPreferredSize(new Dimension(width, height));
            frame.getContentPane().add(panel);
            // pack() ajusta el frame para que el área de contenido coincida con preferredSize
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
         }
         // En cualquier caso, después de mostrar el frame, intentar aplicar/normalizar display mode
         // (si no hay GraphicsDevice válido, la llamada será ignorada)
         config.applyDisplayModeIfNeeded(gd);
         
          frame.requestFocus();
      }
  }