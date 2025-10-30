package GUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.DisplayMode;

public class config extends JPanel {
    private Image imagenFondo;
    
    private static int volumenMusica = 50;
    private static int volumenEfectos = 50;
    private static boolean pantallaCompleta = true;
    private static String dificultad = "Normal";
    
    private static int resolucionAncho = 1366;
    private static int resolucionAlto = 768;
    // Para restaurar el modo de pantalla original si cambiamos la DisplayMode
    private static DisplayMode previousDisplayMode = null;
    private JComboBox<String> comboResoluciones;
    
    private JSlider sliderMusica;
    private JSlider sliderEfectos;
    private JCheckBox chkPantallaCompleta;
    private JButton btnGuardar;
    private JButton btnRestablecer;
    private JButton btnVolver;
    
    private JFrame parentFrame;

    public config() {
        setLayout(null);
        setBackground(new Color(20, 20, 30));
        
        try {
            imagenFondo = new ImageIcon("src/resources/images/fondo_config.png").getImage();
        } catch (Exception e) {
            imagenFondo = null;
        }
        
        // Configurar componentes con posiciones fijas pero en una ventana de tama�o fijo
        JLabel lblTitulo = new JLabel("CONFIGURACI�N", JLabel.CENTER);
        lblTitulo.setBounds(0, 20, 600, 40);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        add(lblTitulo);
        
        JLabel lblAudio = new JLabel("AUDIO");
        lblAudio.setBounds(50, 80, 500, 25);
        lblAudio.setFont(new Font("Arial", Font.BOLD, 18));
        lblAudio.setForeground(new Color(100, 200, 255));
        add(lblAudio);
        
        JLabel lblMusicaTexto = new JLabel("Volumen M�sica:");
        lblMusicaTexto.setBounds(70, 115, 150, 25);
        lblMusicaTexto.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMusicaTexto.setForeground(Color.WHITE);
        add(lblMusicaTexto);
        
        sliderMusica = new JSlider(0, 100, volumenMusica);
        sliderMusica.setBounds(220, 115, 250, 40);
        sliderMusica.setBackground(new Color(20, 20, 30));
        sliderMusica.setForeground(new Color(100, 200, 255));
        add(sliderMusica);
        
        JLabel lblVolumenMusica = new JLabel(volumenMusica + "%");
        lblVolumenMusica.setBounds(480, 115, 50, 25);
        lblVolumenMusica.setFont(new Font("Arial", Font.BOLD, 14));
        lblVolumenMusica.setForeground(Color.WHITE);
        add(lblVolumenMusica);
        
        sliderMusica.addChangeListener(e -> {
            lblVolumenMusica.setText(sliderMusica.getValue() + "%");
        });
        
        JLabel lblEfectosTexto = new JLabel("Volumen Efectos:");
        lblEfectosTexto.setBounds(70, 155, 150, 25);
        lblEfectosTexto.setFont(new Font("Arial", Font.PLAIN, 14));
        lblEfectosTexto.setForeground(Color.WHITE);
        add(lblEfectosTexto);
        
        sliderEfectos = new JSlider(0, 100, volumenEfectos);
        sliderEfectos.setBounds(220, 155, 250, 40);
        sliderEfectos.setBackground(new Color(20, 20, 30));
        sliderEfectos.setForeground(new Color(100, 200, 255));
        add(sliderEfectos);
        
        JLabel lblVolumenEfectos = new JLabel(volumenEfectos + "%");
        lblVolumenEfectos.setBounds(480, 155, 50, 25);
        lblVolumenEfectos.setFont(new Font("Arial", Font.BOLD, 14));
        lblVolumenEfectos.setForeground(Color.WHITE);
        add(lblVolumenEfectos);
        
        sliderEfectos.addChangeListener(e -> {
            lblVolumenEfectos.setText(sliderEfectos.getValue() + "%");
        });
        
        JLabel lblVideo = new JLabel("VIDEO");
        lblVideo.setBounds(50, 210, 500, 25);
        lblVideo.setFont(new Font("Arial", Font.BOLD, 18));
        lblVideo.setForeground(new Color(100, 200, 255));
        add(lblVideo);
        
        chkPantallaCompleta = new JCheckBox("Pantalla Completa");
        chkPantallaCompleta.setBounds(70, 245, 200, 25);
        chkPantallaCompleta.setFont(new Font("Arial", Font.PLAIN, 14));
        chkPantallaCompleta.setForeground(Color.WHITE);
        chkPantallaCompleta.setBackground(new Color(20, 20, 30));
        chkPantallaCompleta.setSelected(false); // forzada a ventana
        chkPantallaCompleta.setFocusPainted(false);
        chkPantallaCompleta.setEnabled(false); // no permitir cambiar
        add(chkPantallaCompleta);
        
        JLabel lblJuego = new JLabel("JUEGO");
        lblJuego.setBounds(50, 285, 500, 25);
        lblJuego.setFont(new Font("Arial", Font.BOLD, 18));
        lblJuego.setForeground(new Color(100, 200, 255));
        add(lblJuego);
        
        JLabel lblResolTexto = new JLabel("Resoluci�n:");
        lblResolTexto.setBounds(70, 320, 150, 25);
        lblResolTexto.setFont(new Font("Arial", Font.PLAIN, 14));
        lblResolTexto.setForeground(Color.WHITE);
        add(lblResolTexto);
        
        String[] baseRes = {"1366x768"};
        comboResoluciones = new JComboBox<>(baseRes);
        comboResoluciones.setBounds(220, 320, 200, 25);
        comboResoluciones.setBackground(new Color(20, 20, 30));
        comboResoluciones.setForeground(Color.WHITE);
        comboResoluciones.setFont(new Font("Arial", Font.PLAIN, 14));
        comboResoluciones.setEnabled(false); // fijada a 1366x768

        String actualRes = resolucionAncho + "x" + resolucionAlto;
        // Si la resolución actual no está en la lista, agregarla y seleccionarla
        boolean encontrado = false;
        for (int i = 0; i < comboResoluciones.getItemCount(); i++) {
            if (comboResoluciones.getItemAt(i).equals(actualRes)) {
                comboResoluciones.setSelectedIndex(i);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            comboResoluciones.addItem(actualRes);
            comboResoluciones.setSelectedItem(actualRes);
        }
         
        add(comboResoluciones);
        
        btnGuardar = new JButton("GUARDAR");
        btnGuardar.setBounds(100, 375, 130, 40);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 14));
        btnGuardar.setBackground(new Color(50, 150, 50));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        add(btnGuardar);
        
        btnGuardar.addActionListener(e -> guardarConfiguracion());
        
        btnRestablecer = new JButton("RESTABLECER");
        btnRestablecer.setBounds(240, 375, 130, 40);
        btnRestablecer.setFont(new Font("Arial", Font.BOLD, 14));
        btnRestablecer.setBackground(new Color(180, 100, 50));
        btnRestablecer.setForeground(Color.WHITE);
        btnRestablecer.setFocusPainted(false);
        btnRestablecer.setBorderPainted(false);
        add(btnRestablecer);
        
        btnRestablecer.addActionListener(e -> restablecerValores());
        
        btnVolver = new JButton("VOLVER");
        btnVolver.setBounds(380, 375, 130, 40);
        btnVolver.setFont(new Font("Arial", Font.BOLD, 14));
        btnVolver.setBackground(new Color(150, 50, 50));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        add(btnVolver);
        
        btnVolver.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });
        
        agregarEfectoHover(btnGuardar, new Color(50, 150, 50), new Color(70, 200, 70));
        agregarEfectoHover(btnRestablecer, new Color(180, 100, 50), new Color(220, 140, 80));
        agregarEfectoHover(btnVolver, new Color(150, 50, 50), new Color(200, 80, 80));
    }
    
    public void setParentFrame(JFrame frame) {
        this.parentFrame = frame;
    }
    
    private void agregarEfectoHover(JButton btn, Color colorNormal, Color colorHover) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(colorHover);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(colorNormal);
            }
        });
    }
    
    private void guardarConfiguracion() {
        volumenMusica = sliderMusica.getValue();
        volumenEfectos = sliderEfectos.getValue();
        // Forzar valores fijos: siempre ventana y 1366x768
        pantallaCompleta = false;
        resolucionAncho = 1366;
        resolucionAlto = 768;
        
        // Aplicar cambios inmediatamente
        aplicarCambiosInmediatos();
        
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
        
        JOptionPane.showMessageDialog(this,
            "Configuración aplicada correctamente",
            "Configuración Guardada",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void aplicarCambiosInmediatos() {
         if (parentFrame != null) {
             GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
             GraphicsDevice gd = ge.getDefaultScreenDevice();

             // Cerrar frame actual
             parentFrame.dispose();

             // Crear nuevo frame con nueva configuraci�n
             JFrame nuevoFrame = new JFrame("Proyecto Final");
             nuevoFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
             // Usar undecorated solo si se pide pantalla completa
             nuevoFrame.setUndecorated(pantallaCompleta);

             // Obtener tama�o de pantalla y ajustar si es necesario
             Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
             int targetWidth = resolucionAncho;
             int targetHeight = resolucionAlto;

             if (pantallaCompleta) {
                 // En pantalla completa usamos el tama�o de pantalla
                 targetWidth = screenSize.width;
                 targetHeight = screenSize.height;
             } else {
                 // En modo ventana, no permitir que la resoluci�n sea mayor que la pantalla
                 targetWidth = Math.min(targetWidth, screenSize.width);
                 targetHeight = Math.min(targetHeight, screenSize.height);
             }

             // Establecer tama�o y contenido antes de mostrar
             nuevoFrame.setSize(targetWidth, targetHeight);
             nuevoFrame.setLocationRelativeTo(null);
             nuevoFrame.getContentPane().add(new ventanaInicio(nuevoFrame));
             nuevoFrame.setResizable(false);

             if (pantallaCompleta && gd.isFullScreenSupported()) {
                 // Mostrar la ventana antes de solicitar el modo exclusivo en algunas plataformas
                 nuevoFrame.setVisible(true);
                 // Poner en modo pantalla completa (ventana no decorada ya aplicada)
                 try {
                     gd.setFullScreenWindow(nuevoFrame);
                 } catch (Exception ex) {
                     // fallback: intentar maximizar si no se puede entrar en modo exclusivo
                     nuevoFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                 }
                 // Intentar cambiar la DisplayMode a la resolución seleccionada, si es soportado
                boolean ok = applyDisplayModeIfNeeded(nuevoFrame);
                if (!ok) {
                    System.out.println("[config] display mode not applied; using borderless fallback");
                    // recreate as borderless fullscreen to ensure content fills the screen
                    nuevoFrame.dispose();
                    nuevoFrame = createBorderlessFullscreen(gd);
                }
             } else {
                 // Si no soporta full screen, o estamos en modo ventana, mostrar normalmente
                 if (pantallaCompleta) {
                     // Forzar maximizado si no hay soporte de full screen
                     nuevoFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                 }
                 nuevoFrame.setVisible(true);
             }

             // Llamar a applyDisplayModeIfNeeded independientemente para que, si estamos saliendo
             // de pantalla completa, se restaure la DisplayMode previa; si estamos entrando, no hará nada
             // hasta que haya un full-screen window (applyDisplayModeIfNeeded maneja ambas ramas).
             applyDisplayModeIfNeeded(nuevoFrame);

             nuevoFrame.requestFocus();
         }
     }

    // Crea un JFrame borderless que ocupa toda la pantalla y devuelve el nuevo frame
    private JFrame createBorderlessFullscreen(GraphicsDevice gd) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
       JFrame frame = new JFrame("Proyecto Final");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.getContentPane().add(new ventanaInicio(frame));
        frame.setBounds(0, 0, screenSize.width, screenSize.height);
       frame.setVisible(true);
        // intentar setFullScreenWindow si es soportado para mejorar compatibilidad
        try {
            if (gd != null && gd.isFullScreenSupported()) gd.setFullScreenWindow(frame);
        } catch (Exception ignore) {}
       return frame;
   }
    
    // Intenta cambiar la DisplayMode del GraphicsDevice a la resoluci�n seleccionada
    // Hacemos pública la función para permitir llamadas desde otros lugares (p.ej. ventanaInicio)
    // Intenta aplicar/restore DisplayMode. Retorna true si cambió el modo (o restauró), false si no
    public static boolean applyDisplayModeIfNeeded(GraphicsDevice gd) {
         if (gd == null) return false;
         try {
            if (gd.isDisplayChangeSupported()) {
                if (pantallaCompleta) {
                    // Buscar el mejor DisplayMode disponible para la resolución solicitada
                    DisplayMode[] modes = gd.getDisplayModes();
                    DisplayMode best = null;
                    int bestScore = Integer.MAX_VALUE;
                    for (DisplayMode m : modes) {
                        int w = m.getWidth();
                        int h = m.getHeight();
                        if (w <= 0 || h <= 0) continue;
                        int score = Math.abs(w - resolucionAncho) + Math.abs(h - resolucionAlto);
                        // Preferir exactitud
                        if (w == resolucionAncho && h == resolucionAlto) {
                            best = m;
                            bestScore = 0;
                            break;
                        }
                        if (score < bestScore) {
                            bestScore = score;
                            best = m;
                        }
                    }
                    if (best != null) {
                        try {
                            if (previousDisplayMode == null) previousDisplayMode = gd.getDisplayMode();
                            System.out.println("[config] changing display mode to: " + best.getWidth() + "x" + best.getHeight() + " @" + best.getRefreshRate() + "Hz, bitDepth=" + best.getBitDepth());
                            gd.setDisplayMode(best);
                            return true;
                        } catch (Exception ex) {
                            // si falla, ignorar y seguir con el modo actual
                            System.out.println("[config] failed to set display mode: " + ex.getMessage());
                        }
                    }
                } else {
                    // Restaurar el modo anterior si existe
                    if (previousDisplayMode != null) {
                        try { gd.setFullScreenWindow(null); } catch (Exception ignore) {}
                        try {
                            System.out.println("[config] restoring previous display mode: " + previousDisplayMode.getWidth() + "x" + previousDisplayMode.getHeight());
                            gd.setDisplayMode(previousDisplayMode);
                            return true;
                        } catch (Exception ex) {
                            System.out.println("[config] failed to restore display mode: " + ex.getMessage());
                        }
                        previousDisplayMode = null;
                    }
                }
            }
         } catch (Exception ex) {
             // No hacemos nada si falla: fallback a full-screen sin cambiar display mode
             System.out.println("[config] applyDisplayModeIfNeeded exception: " + ex.getMessage());
         }
         return false;
      }
    
    // Sobrecarga: intenta aplicar el display mode en el GraphicsDevice asociado a la ventana
    // Retorna true si se pudo aplicar/restaurar, false si no
    public static boolean applyDisplayModeIfNeeded(java.awt.Window window) {
        if (window == null) {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            return applyDisplayModeIfNeeded(gd);
        }
        GraphicsDevice gd = null;
        try {
            if (window.getGraphicsConfiguration() != null) {
                gd = window.getGraphicsConfiguration().getDevice();
            }
        } catch (Exception e) {
            gd = null;
        }
        if (gd == null) gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        return applyDisplayModeIfNeeded(gd);
    }
    
    private void restablecerValores() {
        sliderMusica.setValue(50);
        sliderEfectos.setValue(50);
        chkPantallaCompleta.setSelected(false);
        comboResoluciones.setSelectedItem("1366x768");
        
        JOptionPane.showMessageDialog(this, 
            "Valores restablecidos a predeterminados", 
            "Restablecer", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static int getResolucionAncho() {
        return resolucionAncho;
    }

    public static int getResolucionAlto() {
        return resolucionAlto;
    }
    
    public static void mostrarVentanaConfig(JFrame parentFrame) {
        JFrame configFrame = new JFrame("Configuraci�n");
        config configPanel = new config();
        configPanel.setParentFrame(parentFrame);

        configFrame.setUndecorated(true);
        configFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        configFrame.setSize(600, 450);
        configFrame.setLocationRelativeTo(null);
        configFrame.getContentPane().add(configPanel);
        configFrame.setVisible(true);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
