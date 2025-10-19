package GUI;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import GUI.direccionadorPantalla;

public class dialogo3 extends JPanel {
    private JFrame parentFrame;
    private Image imagenDialogo;
    private float alpha = 1.0f;
    private Timer fadeTimer;
    private Timer animacionTimer;
    private Timer textoTimer;

    // Variables animación de personajes
    private int offsetY1 = 0, offsetY2 = 0;
    private boolean subiendo1 = true, subiendo2 = false;
    private boolean fadeCompletado = false;

    // Texto del diálogo
    private String textoCompleto = "Joya Matteo. Quedate tranquilo, yo me encargo.";
    private String textoMostrado = "";
    private int indiceTexto = 0;
    private boolean textoCompletado = false;

    public dialogo3(JFrame frame) {
        this.parentFrame = frame;
        setLayout(null);
        setBackground(Color.BLACK);

        imagenDialogo = new ImageIcon("src/resources/images/dialogoVacioBombrito.png").getImage();
    
    
        fadeCompletado = true;
        iniciarAnimacionPersonajes();
        iniciarTexto();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (fadeCompletado && textoCompletado) {
                    avanzarAlJuego();
                } else if (fadeCompletado && !textoCompletado) {
                    // Si el texto aún se está escribiendo, mostrar todo de golpe
                    textoMostrado = textoCompleto;
                    textoCompletado = true;
                    textoTimer.stop();
                    repaint();
                }
            }
        });

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (fadeCompletado && textoCompletado &&
                    (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER)) {
                    avanzarAlJuego();
                } else if (fadeCompletado && !textoCompletado) {
                    textoMostrado = textoCompleto;
                    textoCompletado = true;
                    textoTimer.stop();
                    repaint();
                }
            }
        });
        requestFocusInWindow();
    }

    private void iniciarTexto() {
        // Asegurar que el panel tenga el foco
        requestFocusInWindow();
        
        // Timer que va agregando una letra cada 40 ms
        textoTimer = new Timer(40, e -> {
            if (indiceTexto < textoCompleto.length()) {
                textoMostrado += textoCompleto.charAt(indiceTexto);
                indiceTexto++;
                repaint();
            } else {
                textoCompletado = true;
                textoTimer.stop();
            }
        });
        textoTimer.start();
    }

    private void iniciarAnimacionPersonajes() {
        animacionTimer = new Timer(50, e -> {
            if (subiendo1) {
                offsetY1--;
                if (offsetY1 <= -8) subiendo1 = false;
            } else {
                offsetY1++;
                if (offsetY1 >= 0) subiendo1 = true;
            }

            if (subiendo2) {
                offsetY2--;
                if (offsetY2 <= -8) subiendo2 = false;
            } else {
                offsetY2++;
                if (offsetY2 >= 0) subiendo2 = true;
            }

            repaint();
        });
        animacionTimer.start();
    }

    private void avanzarAlJuego() {
        // cortar timers
        if (fadeTimer != null && fadeTimer.isRunning()) fadeTimer.stop();
        if (animacionTimer != null && animacionTimer.isRunning()) animacionTimer.stop();
        if (textoTimer != null && textoTimer.isRunning()) textoTimer.stop();

        // transicionar con fade al map

		calle panelCalle = new calle();
		parentFrame.getContentPane().removeAll();
		parentFrame.getContentPane().add(panelCalle);
		parentFrame.revalidate();
		parentFrame.repaint();
		SwingUtilities.invokeLater(panelCalle::requestFocusInWindow);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (imagenDialogo != null) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int imgWidth = imagenDialogo.getWidth(this);
            int imgHeight = imagenDialogo.getHeight(this);

            double escala = Math.min((double) panelWidth / imgWidth, (double) panelHeight / imgHeight) * 0.9;
            int nuevoAncho = (int) (imgWidth * escala);
            int nuevoAlto = (int) (imgHeight * escala);
            int x = (panelWidth - nuevoAncho) / 2;
            int y = (panelHeight - nuevoAlto) / 2;

            if (fadeCompletado && animacionTimer != null && animacionTimer.isRunning()) {
                y += (offsetY1 + offsetY2) / 2;
            }

            g2d.drawImage(imagenDialogo, x, y, nuevoAncho, nuevoAlto, this);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

            // Mostrar el texto del diálogo con saltos de línea automáticos
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Pixel Arial 11", Font.PLAIN, 22));

            // Coordenadas del cuadro de texto
            int margenIzquierdo = 220;
            int margenSuperior = 640;
            int anchoMaximo = nuevoAncho - 400; // Ancho disponible para el texto
            int alturaLinea = 30; // Espacio entre líneas

            // Dividir el texto en líneas que quepan en el ancho disponible
            FontMetrics fm = g2d.getFontMetrics();
            String[] palabras = textoMostrado.split(" ");
            StringBuilder lineaActual = new StringBuilder();
            int textoY = y + margenSuperior;

            for (String palabra : palabras) {
                String pruebaLinea = lineaActual.length() == 0 ? palabra : lineaActual + " " + palabra;
                int anchoLinea = fm.stringWidth(pruebaLinea);

                if (anchoLinea <= anchoMaximo) {
                    if (lineaActual.length() > 0) lineaActual.append(" ");
                    lineaActual.append(palabra);
                } else {
                    // Dibujar la línea actual
                    g2d.drawString(lineaActual.toString(), x + margenIzquierdo, textoY);
                    textoY += alturaLinea;
                    // Empezar nueva línea
                    lineaActual = new StringBuilder(palabra);
                }
            }
            // Dibujar la última línea
            if (lineaActual.length() > 0) {
                g2d.drawString(lineaActual.toString(), x + margenIzquierdo, textoY);
            }

            // Indicador para continuar
            if (textoCompletado) {
                g2d.setColor(new Color(255, 255, 255, (int)(Math.sin(System.currentTimeMillis() / 300.0) * 127 + 128)));
                g2d.setFont(new Font("Arial", Font.BOLD, 18));
                String texto = "Haz CLIC para continuar";
                int textoWidth1 = g2d.getFontMetrics().stringWidth(texto);
                g2d.drawString(texto, (panelWidth - textoWidth1) / 2, panelHeight - 40);
            }
        }
    }


    public void detenerAnimaciones() {
        if (fadeTimer != null && fadeTimer.isRunning()) fadeTimer.stop();
        if (animacionTimer != null && animacionTimer.isRunning()) animacionTimer.stop();
        if (textoTimer != null && textoTimer.isRunning()) textoTimer.stop();
    }
}