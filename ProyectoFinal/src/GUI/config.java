package GUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class config extends JPanel {
    private Image imagenFondo;
    
    // Variables de configuración
    private static int volumenMusica = 50;
    private static int volumenEfectos = 50;
    private static boolean pantallaCompleta = true; // Cambiado a true por defecto
    private static String dificultad = "Normal";
    
    // Nueva: resolución (como en CS 1.6)
    private static int resolucionAncho = 800;
    private static int resolucionAlto = 600;
    private JComboBox<String> comboResoluciones;
    private JLabel lblResolucion;
    
    // Componentes
    private JSlider sliderMusica;
    private JSlider sliderEfectos;
    private JLabel lblVolumenMusica;
    private JLabel lblVolumenEfectos;
    private JCheckBox chkPantallaCompleta;
    private JButton btnGuardar;
    private JButton btnRestablecer;
    private JButton btnVolver;
    
    private JFrame parentFrame; // Referencia al frame de configuración
    
    public config() {
        setLayout(null);
        setBackground(new Color(20, 20, 30));
        
        // Intentar cargar imagen de fondo (opcional)
        try {
            imagenFondo = new ImageIcon("src/resources/images/fondo_config.png").getImage();
        } catch (Exception e) {
            imagenFondo = null;
        }
        
        // Título
        JLabel lblTitulo = new JLabel("CONFIGURACION", JLabel.CENTER);
        lblTitulo.setBounds(0, 20, 600, 40);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        add(lblTitulo);
        
        // ===== SECCIÓN AUDIO =====
        JLabel lblAudio = new JLabel("AUDIO");
        lblAudio.setBounds(50, 80, 500, 25);
        lblAudio.setFont(new Font("Arial", Font.BOLD, 18));
        lblAudio.setForeground(new Color(100, 200, 255));
        add(lblAudio);
        
        // Volumen Música
        JLabel lblMusicaTexto = new JLabel("Volumen Música:");
        lblMusicaTexto.setBounds(70, 115, 150, 25);
        lblMusicaTexto.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMusicaTexto.setForeground(Color.WHITE);
        add(lblMusicaTexto);
        
        sliderMusica = new JSlider(0, 100, volumenMusica);
        sliderMusica.setBounds(220, 115, 250, 40);
        sliderMusica.setBackground(new Color(20, 20, 30));
        sliderMusica.setForeground(new Color(100, 200, 255));
        add(sliderMusica);
        
        lblVolumenMusica = new JLabel(volumenMusica + "%");
        lblVolumenMusica.setBounds(480, 115, 50, 25);
        lblVolumenMusica.setFont(new Font("Arial", Font.BOLD, 14));
        lblVolumenMusica.setForeground(Color.WHITE);
        add(lblVolumenMusica);
        
        sliderMusica.addChangeListener(e -> {
            lblVolumenMusica.setText(sliderMusica.getValue() + "%");
        });
        
        // Volumen Efectos
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
        
        lblVolumenEfectos = new JLabel(volumenEfectos + "%");
        lblVolumenEfectos.setBounds(480, 155, 50, 25);
        lblVolumenEfectos.setFont(new Font("Arial", Font.BOLD, 14));
        lblVolumenEfectos.setForeground(Color.WHITE);
        add(lblVolumenEfectos);
        
        sliderEfectos.addChangeListener(e -> {
            lblVolumenEfectos.setText(sliderEfectos.getValue() + "%");
        });
        
        // ===== SECCIÓN VIDEO =====
        JLabel lblVideo = new JLabel("VIDEO");
        lblVideo.setBounds(50, 210, 500, 25);
        lblVideo.setFont(new Font("Arial", Font.BOLD, 18));
        lblVideo.setForeground(new Color(100, 200, 255));
        add(lblVideo);
        
        // Pantalla Completa
        chkPantallaCompleta = new JCheckBox("Pantalla Completa");
        chkPantallaCompleta.setBounds(70, 245, 200, 25);
        chkPantallaCompleta.setFont(new Font("Arial", Font.PLAIN, 14));
        chkPantallaCompleta.setForeground(Color.WHITE);
        chkPantallaCompleta.setBackground(new Color(20, 20, 30));
        chkPantallaCompleta.setSelected(pantallaCompleta);
        chkPantallaCompleta.setFocusPainted(false);
        add(chkPantallaCompleta);
        
        // ===== SECCIÓN JUEGO =====
        JLabel lblJuego = new JLabel("JUEGO");
        lblJuego.setBounds(50, 285, 500, 25);
        lblJuego.setFont(new Font("Arial", Font.BOLD, 18));
        lblJuego.setForeground(new Color(100, 200, 255));
        add(lblJuego);
        
        // Resolución tipo CS 1.6
        JLabel lblResolTexto = new JLabel("Resolución:");
        lblResolTexto.setBounds(70, 320, 150, 25);
        lblResolTexto.setFont(new Font("Arial", Font.PLAIN, 14));
        lblResolTexto.setForeground(Color.WHITE);
        add(lblResolTexto);
        
        String[] baseRes = {"640x480", "800x600", "1024x768", "1280x720", "1280x1024"};
       
        String[] resoluciones = baseRes;
        comboResoluciones = new JComboBox<>(resoluciones);
        comboResoluciones.setBounds(220, 320, 200, 25);
        comboResoluciones.setBackground(new Color(20, 20, 30));
        comboResoluciones.setForeground(Color.WHITE);
        comboResoluciones.setFont(new Font("Arial", Font.PLAIN, 14));
        // Seleccionar la resolución actual si está en la lista, si no añadirla
        String actualRes = resolucionAncho + "x" + resolucionAlto;
        boolean contiene = false;
        for (String r : resoluciones) if (r.equals(actualRes)) { contiene = true; break; }
        if (!contiene) comboResoluciones.addItem(actualRes);
        comboResoluciones.setSelectedItem(actualRes);
        add(comboResoluciones);
 
         lblResolucion = new JLabel(resolucionAncho + "x" + resolucionAlto);
         lblResolucion.setBounds(430, 320, 100, 25);
         lblResolucion.setFont(new Font("Arial", Font.BOLD, 14));
         lblResolucion.setForeground(Color.WHITE);
         add(lblResolucion);
 
         comboResoluciones.addActionListener(e -> {
             String s = (String) comboResoluciones.getSelectedItem();
             lblResolucion.setText(s);
         });
        
        // ===== BOTONES =====
        // Botón Guardar
        btnGuardar = new JButton("GUARDAR");
        btnGuardar.setBounds(100, 375, 130, 40);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 14));
        btnGuardar.setBackground(new Color(50, 150, 50));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        add(btnGuardar);
        
        btnGuardar.addActionListener(e -> guardarConfiguracion());
        
        // Botón Restablecer
        btnRestablecer = new JButton("RESTABLECER");
        btnRestablecer.setBounds(240, 375, 130, 40);
        btnRestablecer.setFont(new Font("Arial", Font.BOLD, 14));
        btnRestablecer.setBackground(new Color(180, 100, 50));
        btnRestablecer.setForeground(Color.WHITE);
        btnRestablecer.setFocusPainted(false);
        btnRestablecer.setBorderPainted(false);
        add(btnRestablecer);
        
        btnRestablecer.addActionListener(e -> restablecerValores());
        
        // Botón Volver
        btnVolver = new JButton("VOLVER");
        btnVolver.setBounds(380, 375, 130, 40);
        btnVolver.setFont(new Font("Arial", Font.BOLD, 14));
        btnVolver.setBackground(new Color(150, 50, 50));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        add(btnVolver);
        
        btnVolver.addActionListener(e -> {
            // Salir de pantalla completa de la ventana de configuración
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                // Si está en pantalla completa, quitarla primero
                if (gd.getFullScreenWindow() == window) {
                    gd.setFullScreenWindow(null);
                }
                window.dispose();
            }
            
            // Restaurar pantalla completa del juego principal si es necesario
            if (parentFrame != null && pantallaCompleta) {
                if (gd.isFullScreenSupported()) {
                    gd.setFullScreenWindow(parentFrame);
                }
            }
        });
        
        // Efectos hover para los botones
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
        boolean pantallaCompletaAnterior = pantallaCompleta;
        
        volumenMusica = sliderMusica.getValue();
        volumenEfectos = sliderEfectos.getValue();
        pantallaCompleta = chkPantallaCompleta.isSelected();
        // dificultad UI removed; keep current value (default "Normal") or change programmatically if needed
        
        // Obtener la resolución seleccionada y guardarla
        try {
            String res = (String) comboResoluciones.getSelectedItem();
            String[] partes = res.split("x");
            resolucionAncho = Integer.parseInt(partes[0]);
            resolucionAlto = Integer.parseInt(partes[1]);
        } catch (Exception ex) {
            // Si hay algún error, dejar la resolución por defecto
            resolucionAncho = 800;
            resolucionAlto = 600;
        }
        
        // Aplicar cambio de pantalla completa si cambió
        if (pantallaCompletaAnterior != pantallaCompleta && parentFrame != null) {
            aplicarPantallaCompleta();
        }
        
        // Aplicar la resolución (si no está en pantalla completa se cambia el tamaño)
        aplicarResolucion();
        
        // Aplicar otros cambios
        aplicarConfiguracion();
        
        // Cerrar la ventana de configuración para evitar problemas de foco después de cambiar resolución
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
        // Mostrar confirmación con parentFrame como padre para que aparezca en la ventana correcta
        if (parentFrame != null) {
            JOptionPane.showMessageDialog(parentFrame,
                "Configuración guardada correctamente",
                "Guardado",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Configuración guardada correctamente",
                "Guardado",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void aplicarPantallaCompleta() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        
        if (pantallaCompleta) {
            // Activar pantalla completa
            parentFrame.dispose();
            parentFrame.setUndecorated(true);
            // Preferir poner la ventana como full-screen antes de hacerla visible
            if (gd.isFullScreenSupported()) {
                gd.setFullScreenWindow(parentFrame);
            }
            parentFrame.setVisible(true);
            // Forzar que la ventana principal reciba foco (importante para que funcionen botones/teclas)
            parentFrame.toFront();
            parentFrame.requestFocus();
            SwingUtilities.invokeLater(() -> {
                parentFrame.requestFocusInWindow();
                parentFrame.getContentPane().requestFocusInWindow();
            });
            if (!gd.isFullScreenSupported()) {
                parentFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
            // Asegurar entrada y foco
            ensureInputEnabled(parentFrame);
          } else {
             // Desactivar pantalla completa
            if (gd.getFullScreenWindow() == parentFrame) {
                gd.setFullScreenWindow(null);
            }

            parentFrame.dispose();
            parentFrame.setUndecorated(false);
            parentFrame.setSize(resolucionAncho, resolucionAlto);
            parentFrame.setLocationRelativeTo(null);
            parentFrame.setVisible(true);
            // Forzar foco tras cambiar el estado
            parentFrame.toFront();
            parentFrame.requestFocus();
            SwingUtilities.invokeLater(() -> {
                parentFrame.requestFocusInWindow();
                parentFrame.getContentPane().requestFocusInWindow();
            });
            ensureInputEnabled(parentFrame);
          }
      }
    
    // Nueva: aplica la resolución cuando esté en modo ventana
    private void aplicarResolucion() {
         if (parentFrame == null) return;
         GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         GraphicsDevice gd = ge.getDefaultScreenDevice();
         
         if (!pantallaCompleta) {
             // Modo ventana: simplemente ajustar tamaño
             parentFrame.dispose();
             parentFrame.setUndecorated(false);
             parentFrame.setSize(resolucionAncho, resolucionAlto);
             parentFrame.setLocationRelativeTo(null);
             parentFrame.setVisible(true);
             // Forzar foco al volver la ventana
             parentFrame.toFront();
             parentFrame.requestFocus();
             SwingUtilities.invokeLater(() -> {
                 parentFrame.requestFocusInWindow();
                 parentFrame.getContentPane().requestFocusInWindow();
             });
         } else {
             // Modo pantalla completa: intentar cambiar DisplayMode si es soportado
             if (gd.isFullScreenSupported()) {
                 try {
                     DisplayMode dm = buscarDisplayModeCompatible(resolucionAncho, resolucionAlto);
                     // Poner la ventana en full-screen antes de cambiar DisplayMode ayuda en algunas plataformas
                     gd.setFullScreenWindow(parentFrame);
                     if (gd.isDisplayChangeSupported() && dm != null) {
                         try {
                             gd.setDisplayMode(dm);
                             // Asegurar foco tras cambiar modo de pantalla
                             parentFrame.toFront();
                             parentFrame.requestFocus();
                             SwingUtilities.invokeLater(() -> {
                                 parentFrame.requestFocusInWindow();
                                 parentFrame.getContentPane().requestFocusInWindow();
                             });
                         } catch (Exception ex) {
                             // No se pudo cambiar el modo de pantalla, seguir en full-screen nativo
                             System.err.println("No se pudo cambiar DisplayMode: " + ex.getMessage());
                         }
                     }
                     parentFrame.validate();
                     parentFrame.repaint();
                     // Forzar foco en caso de que pierda entrada
                     parentFrame.toFront();
                     parentFrame.requestFocus();
                     SwingUtilities.invokeLater(() -> {
                         parentFrame.requestFocusInWindow();
                         parentFrame.getContentPane().requestFocusInWindow();
                     });
                     // Asegurar que la ventana recibe entrada y que no hay un glass pane bloqueando
                     ensureInputEnabled(parentFrame);
                 } catch (Exception ex) {
                     // Fallback: maximizar
                     parentFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                     parentFrame.setVisible(true);
                 }
             } else {
                 // Si no hay soporte full-screen, maximizamos
                 parentFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                 parentFrame.setVisible(true);
                 parentFrame.toFront();
                 parentFrame.requestFocus();
                 SwingUtilities.invokeLater(() -> {
                     parentFrame.requestFocusInWindow();
                     parentFrame.getContentPane().requestFocusInWindow();
                 });
                 ensureInputEnabled(parentFrame);
             }
         }
      }
    
    private void aplicarConfiguracion() {
        // Aquí aplicarás los cambios reales al juego
        // Por ejemplo:
        // Musica.setVolumen(volumenMusica);
    }
    
    private void restablecerValores() {
        sliderMusica.setValue(50);
        sliderEfectos.setValue(50);
        chkPantallaCompleta.setSelected(true);
        // dificultad reset to default
        dificultad = "Normal";
        comboResoluciones.setSelectedItem("800x600");
        resolucionAncho = 800;
        resolucionAlto = 600;
        
        JOptionPane.showMessageDialog(this, 
            "Valores restablecidos a predeterminados", 
            "Restablecer", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Getters para acceder a la configuración desde otras clases
    public static int getVolumenMusica() {
        return volumenMusica;
    }
    
    public static int getVolumenEfectos() {
        return volumenEfectos;
    }
    
    public static boolean isPantallaCompleta() {
        return pantallaCompleta;
    }
    
    public static String getDificultad() {
        return dificultad;
    }
    
    // Nuevos getters para resolución
    public static int getResolucionAncho() {
        return resolucionAncho;
    }

    public static int getResolucionAlto() {
        return resolucionAlto;
    }
    
    // Método estático para abrir la ventana de configuración
    public static void mostrarVentanaConfig(JFrame parentFrame) {
        JFrame configFrame = new JFrame("Configuración");
        config configPanel = new config();
        configPanel.setParentFrame(parentFrame);
        
        configFrame.setUndecorated(true);
        configFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        
        configFrame.getContentPane().add(configPanel);
        
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(configFrame);
            // Asegurar que la ventana es visible (algunos SO/VM requieren setVisible incluso en full-screen)
            configFrame.setVisible(true);
         } else {
             configFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
             configFrame.setVisible(true);
         }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // Buscar un DisplayMode compatible con el ancho/alto deseados.
    private DisplayMode buscarDisplayModeCompatible(int ancho, int alto) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        DisplayMode[] dms = gd.getDisplayModes();
        DisplayMode mejor = null;
        int mejorProfundidad = -1;
        int mejorRefresh = -1;

        for (DisplayMode dm : dms) {
            if (dm.getWidth() == ancho && dm.getHeight() == alto) {
                int depth = dm.getBitDepth();
                int refresh = dm.getRefreshRate();
                // Preferir mayor profundidad de color y mayor refresh
                if (depth > mejorProfundidad || (depth == mejorProfundidad && refresh > mejorRefresh) || mejor == null) {
                    mejor = dm;
                    mejorProfundidad = depth;
                    mejorRefresh = refresh;
                }
            }
        }
        return mejor;
    }

    // Forzar que la ventana y sus componentes acepten entrada: ocultar glassPane, habilitar componentes y asegurar focusableWindowState
    private void ensureInputEnabled(Window wnd) {
        if (wnd == null) return;
        try {
            if (wnd instanceof JFrame) {
                JFrame f = (JFrame) wnd;
                Component glass = f.getGlassPane();
                if (glass != null) {
                    glass.setVisible(false);
                    glass.setEnabled(false);
                }
                // Asegurar que la ventana acepta foco
                f.setFocusableWindowState(true);
                f.toFront();
                f.requestFocus();
                SwingUtilities.invokeLater(() -> {
                    f.requestFocusInWindow();
                    Container content = f.getContentPane();
                    if (content != null) {
                        for (Component c : content.getComponents()) {
                            try {
                                c.setEnabled(true);
                                c.setFocusable(true);
                            } catch (Exception ignored) {}
                        }
                        content.requestFocusInWindow();
                    }
                });
            }
        } catch (Exception ex) {
            // no bloquear si esto falla
            System.err.println("ensureInputEnabled error: " + ex.getMessage());
        }
    }
}