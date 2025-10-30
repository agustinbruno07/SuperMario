package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.KeyboardFocusManager;
import java.awt.KeyEventDispatcher;

public class casaIzquierda extends JPanel implements KeyListener {
    
    private jugador player;
    private Image fondo;
    private Image imagenCofre;
    private Timer gameLoop;
    private JLabel mensajeLabel; 
    private Timer mensajeTimer; 
    private boolean upPressed, downPressed, leftPressed, rightPressed;
    private boolean estaEnSalida = false;
    private boolean estaEnCofre = false;
    private boolean cofreAbierto = false;
    private colisionCalle colisiones;

    private static final boolean DEBUG = false;
    private JLabel debugLabel;
   
    private boolean ignoreCollisions = false;

    private static final int FIXED_WIDTH = 1366;
    private static final int FIXED_HEIGHT = 768;
    
    private final int cofreX = 357;
    private final int cofreY = 500;
    private final int cofreAncho = 100;
    private final int cofreAlto = 100;

    private KeyEventDispatcher globalDispatcher;

    public casaIzquierda(JFrame parentFrame) {
        setLayout(null);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        // Registrar key bindings para movimiento (más fiable que KeyListener cuando no hay foco)
        setupKeyBindings();

        fondo = new ImageIcon("src/resources/images/casa_izquierda.png").getImage();
        
        colisiones = new colisionCalle("src/resources/images/casa_izquierdaColisiones.png");
            
        if (EstadoJuego.isCofreAbierto()) {
            imagenCofre = new ImageIcon("src/resources/images/cofreAbierto.png").getImage();
            cofreAbierto = true;
        } else {
            imagenCofre = new ImageIcon("src/resources/images/cofre.png").getImage();
        }

        
        player = new jugador(300, 650);

        mensajeLabel = new JLabel("", JLabel.CENTER);
        mensajeLabel.setBounds(200, 100, 400, 60);
        mensajeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mensajeLabel.setForeground(Color.YELLOW);
        mensajeLabel.setBackground(new Color(0, 0, 0, 180));
        mensajeLabel.setOpaque(true);
        mensajeLabel.setVisible(false);
        add(mensajeLabel);

        mensajeTimer = new Timer(2000, e -> {
            mensajeLabel.setVisible(false);
            ((Timer)e.getSource()).stop();
        });
        mensajeTimer.setRepeats(false);

        addKeyListener(this);
        SwingUtilities.invokeLater(this::requestFocusInWindow);

        // Registrar dispatcher global inmediatamente para asegurar captura en fullscreen
        if (globalDispatcher == null) {
            globalDispatcher = new KeyEventDispatcher() {
                @Override
                public boolean dispatchKeyEvent(KeyEvent e) {
                    synchronized (casaIzquierda.this) {
                        if (e.getID() == KeyEvent.KEY_PRESSED) {
                            int k = e.getKeyCode();
                            if (k == KeyEvent.VK_W || k == KeyEvent.VK_UP) upPressed = true;
                            if (k == KeyEvent.VK_S || k == KeyEvent.VK_DOWN) {
                                downPressed = true;
                                if (estaEnSalida) {
                                    JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(casaIzquierda.this);
                                    if (parent != null) volverACalle(parent);
                                }
                            }
                            if (k == KeyEvent.VK_A || k == KeyEvent.VK_LEFT) leftPressed = true;
                            if (k == KeyEvent.VK_D || k == KeyEvent.VK_RIGHT) rightPressed = true;
                        } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                            int k = e.getKeyCode();
                            if (k == KeyEvent.VK_W || k == KeyEvent.VK_UP) upPressed = false;
                            if (k == KeyEvent.VK_S || k == KeyEvent.VK_DOWN) downPressed = false;
                            if (k == KeyEvent.VK_A || k == KeyEvent.VK_LEFT) leftPressed = false;
                            if (k == KeyEvent.VK_D || k == KeyEvent.VK_RIGHT) rightPressed = false;
                        }
                    }
                    return false;
                }
            };
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(globalDispatcher);
        }

        if (DEBUG) {
            debugLabel = new JLabel("", JLabel.LEFT);
            debugLabel.setForeground(Color.WHITE);
            debugLabel.setBackground(new Color(0,0,0,120));
            debugLabel.setOpaque(true);
            debugLabel.setBounds(10, 10, 300, 20);
            add(debugLabel);
        }
        
        gameLoop = new Timer(16, e -> {
            int w = FIXED_WIDTH;
            int h = FIXED_HEIGHT;
            Rectangle bounds = new Rectangle(0, 0, w, h);
            
            double scaleX = (getWidth() > 0) ? (double) getWidth() / FIXED_WIDTH : 1.0;
            double scaleY = (getHeight() > 0) ? (double) getHeight() / FIXED_HEIGHT : 1.0;

            int msgW = Math.max(50, (int) Math.round(400 * scaleX));
            int msgX = (int) Math.round((FIXED_WIDTH * scaleX - msgW) / 2.0);
            mensajeLabel.setBounds(msgX, (int) Math.round(100 * scaleY), msgW, (int) Math.round(60 * scaleY));

            int oldX = player.getX();
            int oldY = player.getY();

            if (upPressed)    player.moveUp();
            if (downPressed)  player.moveDown();
            if (leftPressed)  player.moveLeft();
            if (rightPressed) player.moveRight();

            // Verificar colisiones con la máscara usando dimensiones lógicas (salteada si ignoreCollisions=true)
            if (!ignoreCollisions) {
                if (colisiones.hayColision(player.getBounds(), FIXED_WIDTH, FIXED_HEIGHT)) {
                    player.setPosition(oldX, oldY);
                }
            }

            player.clampTo(bounds);
            verificarPosicionSalida();
            verificarPosicionCofre();
            if (DEBUG && debugLabel != null) {
                Rectangle p = player.getBounds();
                debugLabel.setText("player=(" + p.x + "," + p.y + ") inSalida=" + estaEnSalida + " ignoreCol=" + ignoreCollisions
                    + " up=" + upPressed + " down=" + downPressed + " left=" + leftPressed + " right=" + rightPressed);
                 // actualizar posicion del debugLabel para que escale con pantalla
                 double dsx = (getWidth() > 0) ? (double) getWidth() / FIXED_WIDTH : 1.0;
                 int dbx = 10; int dby = 10;
                 debugLabel.setBounds(dbx, dby, (int)(200 * dsx), 20);
             }
             repaint();
         });
         gameLoop.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // Registrar dispatcher al añadirse el componente a la jerarquía
        if (globalDispatcher == null) {
            globalDispatcher = new KeyEventDispatcher() {
                @Override
                public boolean dispatchKeyEvent(KeyEvent e) {
                    synchronized (casaIzquierda.this) {
                        if (e.getID() == KeyEvent.KEY_PRESSED) {
                            int k = e.getKeyCode();
                            if (k == KeyEvent.VK_W || k == KeyEvent.VK_UP) upPressed = true;
                            if (k == KeyEvent.VK_S || k == KeyEvent.VK_DOWN) {
                                downPressed = true;
                                // Si estamos en la zona de salida y se presiona S, salir
                                if (estaEnSalida) {
                                    JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(casaIzquierda.this);
                                    if (parent != null) volverACalle(parent);
                                }
                            }
                            if (k == KeyEvent.VK_A || k == KeyEvent.VK_LEFT) leftPressed = true;
                            if (k == KeyEvent.VK_D || k == KeyEvent.VK_RIGHT) rightPressed = true;
                        } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                            int k = e.getKeyCode();
                            if (k == KeyEvent.VK_W || k == KeyEvent.VK_UP) upPressed = false;
                            if (k == KeyEvent.VK_S || k == KeyEvent.VK_DOWN) downPressed = false;
                            if (k == KeyEvent.VK_A || k == KeyEvent.VK_LEFT) leftPressed = false;
                            if (k == KeyEvent.VK_D || k == KeyEvent.VK_RIGHT) rightPressed = false;
                        }
                    }
                    return false; // no consumir el evento
                }
            };
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(globalDispatcher);
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        // Quitar dispatcher al retirarse el componente
        if (globalDispatcher != null) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(globalDispatcher);
            globalDispatcher = null;
        }
    }

    private void verificarPosicionSalida() {
        Rectangle jugadorBounds = player.getBounds();
        
        // Coordenadas lógicas de la zona de salida (área amplia para evitar necesidad de pixel-perfect)
        // Ajusta estos valores si necesitas mover la puerta en el sprite.
        int salidaX = 300;
        // Subimos la zona de salida para que esté al alcance del jugador (y max ≈ 704)
        int salidaY = 640;
        int salidaW = 280; // ancho del área de salida
        int salidaH = 130; // alto del área de salida

        Rectangle salidaArea = new Rectangle(salidaX, salidaY, salidaW, salidaH);
        estaEnSalida = jugadorBounds.intersects(salidaArea);
    }

    private void verificarPosicionCofre() {
        Rectangle jugadorBounds = player.getBounds();
        Rectangle cofreBounds = new Rectangle(cofreX, cofreY, cofreAncho, cofreAlto);
     
        estaEnCofre = jugadorBounds.intersects(cofreBounds);
    }

    private void abrirCofre() {
        if (!cofreAbierto) {
            imagenCofre = new ImageIcon("src/resources/images/cofreAbierto.png").getImage();
            cofreAbierto = true;
            
            EstadoJuego.setCofreAbierto(true);
            
            mostrarMensaje("Encontraste una llave");
            
            repaint();
        }
    }

    private void mostrarMensaje(String mensaje) {
        if (!mensajeLabel.isVisible()) {
            mensajeLabel.setText(mensaje);
            mensajeLabel.setVisible(true);
            int x = (getWidth() - mensajeLabel.getWidth()) / 2;
            mensajeLabel.setLocation(x, 100);
            mensajeTimer.start();
        }
    }

    private void volverACalle(JFrame parentFrame) {
        if (gameLoop != null && gameLoop.isRunning()) gameLoop.stop();
        if (mensajeTimer != null && mensajeTimer.isRunning()) mensajeTimer.stop();
        
        // Aparecer en la calle cerca de la entrada izquierda (coordenadas lógicas)
        calle callePanel = new calle(50, 286);
        
        parentFrame.getContentPane().removeAll();
        parentFrame.getContentPane().add(callePanel);
        parentFrame.revalidate();
        parentFrame.repaint();
        SwingUtilities.invokeLater(callePanel::requestFocusInWindow);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Escalar el contexto para dibujar en coordenadas lógicas
        double sx = (getWidth() > 0) ? (double) getWidth() / FIXED_WIDTH : 1.0;
        double sy = (getHeight() > 0) ? (double) getHeight() / FIXED_HEIGHT : 1.0;

        java.awt.geom.AffineTransform original = g2.getTransform();
        g2.scale(sx, sy);

        // Dibujar fondo, cofre y jugador en coordenadas lógicas
        g2.drawImage(fondo, 0, 0, FIXED_WIDTH, FIXED_HEIGHT, null);
        g2.drawImage(imagenCofre, cofreX, cofreY, cofreAncho, cofreAlto, this);
        player.draw(g2);

        // Restaurar transform para que componentes Swing (labels) no se vean afectados
        g2.setTransform(original);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP)    upPressed = true;
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN)  downPressed = true;
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT)  leftPressed = true;
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) rightPressed = true;
        
        if (key == KeyEvent.VK_S && estaEnSalida) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (parentFrame != null) {
                volverACalle(parentFrame);
            }
        }
        
        if (key == KeyEvent.VK_E && estaEnCofre && !cofreAbierto) {
            abrirCofre();
        }
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

    // Key bindings para soportar teclas aunque el panel no tenga foco absoluto
    private void setupKeyBindings() {
        InputMap im = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();

        // Up (W / UP)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "up.press");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "up.release");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "up.press");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "up.release");

        // Down (S / DOWN)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "down.press");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "down.release");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "down.press");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "down.release");

        // Left (A / LEFT)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "left.press");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "left.release");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left.press");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "left.release");

        // Right (D / RIGHT)
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "right.press");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "right.release");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "right.press");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "right.release");

        // Toggle colisiones (C) - press to toggle
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0, false), "toggle.collision");
        am.put("toggle.collision", new AbstractAction(){ public void actionPerformed(ActionEvent e){ ignoreCollisions = !ignoreCollisions; } });

         am.put("up.press", new AbstractAction(){ public void actionPerformed(ActionEvent e){ upPressed = true; } });
         am.put("up.release", new AbstractAction(){ public void actionPerformed(ActionEvent e){ upPressed = false; } });

         am.put("down.press", new AbstractAction(){ public void actionPerformed(ActionEvent e){ downPressed = true; } });
         am.put("down.release", new AbstractAction(){ public void actionPerformed(ActionEvent e){ downPressed = false; } });

         am.put("left.press", new AbstractAction(){ public void actionPerformed(ActionEvent e){ leftPressed = true; } });
         am.put("left.release", new AbstractAction(){ public void actionPerformed(ActionEvent e){ leftPressed = false; } });

         am.put("right.press", new AbstractAction(){ public void actionPerformed(ActionEvent e){ rightPressed = true; } });
         am.put("right.release", new AbstractAction(){ public void actionPerformed(ActionEvent e){ rightPressed = false; } });
    }
}