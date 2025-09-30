package GUI;

import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ventanaPrincipal extends JFrame implements KeyListener {
    private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    private player jugador;
    private JLabel mario;
    private ImageIcon idleRight, walkRight, idleLeft, walkLeft, jumpIcon;
    private Timer gameTimer;
    private boolean leftPressed, rightPressed, facingLeft = false;
    private Image imgFondo, imgSuelo, imgTuberia;
    private int mapOffset = 0;
    private final int GROUND_H = 160;
    private final int MAP_LIMIT = 10000; // Limite reducido para que el jefe no quede inaccesible
    private JLabel numNivel;
    private JLabel Vida;
    private JLabel numVida;
    private ArrayList<Integer> huecos = new ArrayList<>();
    private ArrayList<Rectangle> huecosRect = new ArrayList<>();
    private ArrayList<Rectangle> tuberias = new ArrayList<>();
    private boolean huecosGenerados = false;
    private final int PIPE_EMBED_MIN = 6;
    private final int PIPE_EMBED_MAX = 18;
    private static final int PIPE_PASS_TOL = 6;
    private ArrayList<enemigo> enemigos = new ArrayList<>();
    private ImageIcon enemy;
    private int lastPlayerY = 0;
    private boolean cayendoEnHueco = false;
    private int nivel = 1;
    private Image imgBanderaFinal;
    private Rectangle rectBanderaFinal;
    private enemigo jefeFinal;
    private ImageIcon jefeIcon;
    
    public ventanaPrincipal(int nivel) {
    this.nivel = nivel;
    setSize(1000, 700);
    setResizable(false);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    contentPane = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            drawBackground(g2, imgFondo, mapOffset, 0.35);
            int y = getHeight() - GROUND_H;
            drawGround(g2, imgSuelo, y, GROUND_H, mapOffset);
            drawTuberias(g2, mapOffset);
            drawBanderaFinal(g2, mapOffset);
            if (nivel == 2 && jefeFinal != null) {
                jefeFinal.syncSprite(mapOffset);
            }
            g2.dispose();
        }
    };
    contentPane.setBackground(Color.BLACK);
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(null);
    setContentPane(contentPane);
    imgFondo = new ImageIcon(getFondoPorNivel()).getImage();
    imgSuelo = new ImageIcon("src/resources/suelo.png").getImage();
    imgTuberia = new ImageIcon("src/resources/tuberia.png").getImage();

    jugador = new player(465, 0);

    idleRight = load("src/resources/mario.png");
    walkRight = load("src/resources/marioWalk.png");
    idleLeft = load("src/resources/marioAtras.png");
    walkLeft = load("src/resources/marioWalkAtras.png");
    jumpIcon = load("src/resources/marioJump.png");
    enemy = load ("src/resources/enemigoLVL1.png");
    
    
    mario = new JLabel(idleRight);
    mario.setBounds(jugador.getX(), jugador.getY(), player.WIDTH, player.HEIGHT);
    contentPane.add(mario);

    JLabel Nivel = new JLabel("Nivel:");
    Nivel.setFont(new Font("Tahoma", Font.PLAIN, 21));
    Nivel.setForeground(Color.WHITE);
    Nivel.setBounds(10, 11, 60, 49);
    contentPane.add(Nivel);

    numNivel = new JLabel();
    numNivel.setForeground(Color.WHITE);
    numNivel.setFont(new Font("Tahoma", Font.PLAIN, 21));
    numNivel.setBounds(80, 11, 48, 49);
    numNivel.setText(String.valueOf(nivel));
    contentPane.add(numNivel);

    Vida = new JLabel("Vida:");
    Vida.setForeground(Color.WHITE);
    Vida.setFont(new Font("Tahoma", Font.PLAIN, 21));
    Vida.setBounds(10, 59, 60, 49);
    contentPane.add(Vida);

    numVida = new JLabel("3");
    numVida.setForeground(Color.WHITE);
    numVida.setFont(new Font("Tahoma", Font.PLAIN, 21));
    numVida.setBounds(80, 59, 60, 49);
    contentPane.add(numVida);
    contentPane.repaint();

    ventanaInicio.iniciarMusicaFondo("src/resources/sound/musicaJuego.mp3");

    gameTimer = new Timer(16, e -> {
            int panelW = contentPane.getWidth();
            if (panelW <= 0)
                panelW = getWidth();

            int centerX = panelW / 2 - player.WIDTH / 2;

            int step = 10;

            if (leftPressed) {
                if (jugador.getX() > centerX) {
                    int nuevaX = jugador.getX() - step;
                    if (puedeMoverseA(nuevaX)) {
                        jugador.setX(nuevaX);
                    }
                } else {
                    int nuevoOffset = Math.max(0, mapOffset - step);
                    Rectangle futuro = new Rectangle(nuevoOffset + jugador.getX(), jugador.getY(), player.WIDTH, player.HEIGHT);
                    if (esParedContraTuberia(futuro, jugador.isEnAire())) {
                        Rectangle pared = null;
                        for (Rectangle t : tuberias) if (futuro.intersects(t)) { pared = t; break; }
                        if (pared != null) {
                            nuevoOffset = pared.x + pared.width - jugador.getX();
                        }
                        nuevoOffset = Math.max(0, Math.min(nuevoOffset, MAP_LIMIT - panelW));
                    } 
                    mapOffset = nuevoOffset;
                }
            }

            if (rightPressed) {
                if (jugador.getX() < centerX) {
                    int nuevaX = jugador.getX() + step;
                    if (puedeMoverseA(nuevaX)) {
                        jugador.setX(nuevaX);
                    }
                } else {
                    // Permitir avanzar más allá del MAP_LIMIT si es nivel 2, 3 o 4
                    int limite = (nivel == 2 || nivel == 3 || nivel == 4) ? Integer.MAX_VALUE : MAP_LIMIT - panelW;
                    int nuevoOffset = Math.min(limite, mapOffset + step);
                    Rectangle futuro = new Rectangle(nuevoOffset + jugador.getX(), jugador.getY(), player.WIDTH, player.HEIGHT);
                    if (esParedContraTuberia(futuro, jugador.isEnAire())) {
                        Rectangle pared = null;
                        for (Rectangle t : tuberias) if (futuro.intersects(t)) { pared = t; break; }
                        if (pared != null) {
                            nuevoOffset = pared.x - player.WIDTH - jugador.getX();
                        }
                        nuevoOffset = Math.max(0, Math.min(nuevoOffset, limite));
                    }
                    mapOffset = nuevoOffset;
                }
            }

            int groundY = contentPane.getHeight() - GROUND_H - player.HEIGHT;

            boolean sobreHueco = false;
            int jugadorIzquierda = mapOffset + jugador.getX();
            int jugadorDerecha = jugadorIzquierda + player.WIDTH;
            int jugadorCentro = jugadorIzquierda + player.WIDTH / 2;

            for (Rectangle hueco : huecosRect) {
                if (jugadorDerecha > hueco.x && jugadorIzquierda < hueco.x + hueco.width) {
                    sobreHueco = true;
                    break;
                }
            }

            if (sobreHueco && !jugador.isEnAire()) {
                jugador.setEnAire(true);
                cayendoEnHueco = true;
            }
            
            if (cayendoEnHueco) {
                int groundY1 = contentPane.getHeight() - GROUND_H - player.HEIGHT;
                if (jugador.getY() > groundY1 + 50) {
                    if (!sobreHueco) {
                        perder();
                        return;
                    }
                }
                if (jugador.getY() >= groundY1 && !sobreHueco) {
                    perder();
                    return;
                }
            }
            
            Rectangle tuberiaDebajo = null;
            int mejorTuberiaY = groundY;

            if (!sobreHueco) {
                for (Rectangle tuberia : tuberias) {
                    int margen = 10;
                    if (jugadorCentro >= tuberia.x + margen && jugadorCentro <= tuberia.x + tuberia.width - margen) {
                        int tuberiaTop = tuberia.y - player.HEIGHT;
                        if (jugador.getY() >= tuberiaTop - 15 && jugador.getY() <= tuberiaTop + 15) {
                            tuberiaDebajo = tuberia;
                            mejorTuberiaY = tuberiaTop;
                            break;
                        }
                    }
                }
            }

            int groundYFinal;
            if (sobreHueco) {
                groundYFinal = contentPane.getHeight() + 500;
            } else if (tuberiaDebajo != null) {
                groundYFinal = mejorTuberiaY;
            } else {
                groundYFinal = groundY;
            }

            if (sobreHueco && !jugador.isEnAire()) {
                jugador.setEnAire(true);
            }
            lastPlayerY = jugador.getY();
            jugador.update(groundYFinal);

            if (tuberiaDebajo != null) {
                if (jugador.getY() > mejorTuberiaY) {
                    jugador.setY(mejorTuberiaY);
                    jugador.setEnAire(false);
                }
            } else {
                int sueloNormal = contentPane.getHeight() - GROUND_H - player.HEIGHT;
                if (jugador.getY() < sueloNormal && !jugador.isEnAire()) {
                    jugador.setEnAire(true);
                }
            }

            mario.setBounds(jugador.getX(), jugador.getY(), player.WIDTH, player.HEIGHT);
            contentPane.repaint();

            if (jugador.getY() > getHeight()) {
                perder();
                return;
            }

            if (mapOffset >= MAP_LIMIT - panelW) {
                if (nivel == 2 || nivel == 3 || nivel == 4) {
                    // No hacer nada aquí, solo se gana matando al jefe
                } else {
                    ganar();
                    return;
                }
            }
            int sueloYEnem = contentPane.getHeight() - GROUND_H - enemigo.HEIGHT;

            for (enemigo e1 : enemigos) {
                boolean eSobreHueco = false;
                int eIzq = e1.getWorldX();
                int eDer = eIzq + enemigo.WIDTH;
                int eCentro = eIzq + enemigo.WIDTH / 2;

                boolean caeriaEnHueco = false;
                int nextX = eIzq + (e1.esJefe() && e1.getVidas() == 5 && e1.getWorldX() != 0 ? 0 : e1.getWorldX() < eCentro ? -3 : 3);
                for (Rectangle hueco : huecosRect) {
                    if (eDer > hueco.x && eIzq < hueco.x + hueco.width) { eSobreHueco = true; break; }
                    if (nextX + enemigo.WIDTH > hueco.x && nextX < hueco.x + hueco.width) {
                        caeriaEnHueco = true;
                    }
                }
                if (caeriaEnHueco) {
                    e1.cambiarDireccion();
                }
                Rectangle tuberiaDebajoE = null;
                int mejorY = sueloYEnem;
                if (!eSobreHueco) {
                    for (Rectangle tub : tuberias) {
                        int margen = 10;
                        if (eCentro >= tub.x + margen && eCentro <= tub.x + tub.width - margen) {
                            int tubTop = tub.y - enemigo.HEIGHT;
                            if (e1.getY() >= tubTop - 15 && e1.getY() <= tubTop + 15) {
                                tuberiaDebajoE = tub; mejorY = tubTop; break;
                            }
                        }
                    }
                }
                int groundYEnemigo = eSobreHueco ? mejorY : (tuberiaDebajoE != null ? mejorY : sueloYEnem);
                int playerWorldX = mapOffset + jugador.getX();
                
                // Pasa el límite del mapa a la función de actualización del enemigo
                e1.updateHorizontal(tuberias, MAP_LIMIT, playerWorldX); 
                e1.updateVertical(groundYEnemigo);
                e1.syncSprite(mapOffset);
            }
            
            Rectangle jugadorWorld = new Rectangle(mapOffset + jugador.getX(), jugador.getY(), player.WIDTH, player.HEIGHT);
            java.util.List<enemigo> paraEliminar = new ArrayList<>();

            for (enemigo e1 : enemigos) {
                Rectangle enemyWorld = e1.getWorldRect();
                if (jugadorWorld.intersects(enemyWorld)) {
                    int enemyTop = e1.getY();
                    int playerTop = jugador.getY();
                    int playerBottom = jugador.getY() + player.HEIGHT;
                    int prevBottom = lastPlayerY + player.HEIGHT;

                    int TOL = 6;
                    boolean desdeArriba = prevBottom <= enemyTop + TOL && playerBottom >= enemyTop;

                    if (desdeArriba) {
                        if (e1.esJefe()) {
                            if (e1.getVidas() > 1) {
                                e1.restarVida();
                            } else {
                                paraEliminar.add(e1);
                            }
                        } else {
                            paraEliminar.add(e1);
                        }

                        jugador.setY(enemyTop - player.HEIGHT);
                        if (jugador.isEnAire()) {
                            jugador.setEnAire(false);
                        }
                        jugador.jump();

                    } else {
                        perder();
                        return;
                    }
                }
            }

            if (!paraEliminar.isEmpty()) {
                for (enemigo e1 : paraEliminar) {
                    contentPane.remove(e1.getLabel());
                    enemigos.remove(e1);
                    if (e1.esJefe()) {
                        if (nivel == 3) {
                            JFrame frame = new JFrame();
                            frame.setSize(1000, 700);
                            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            frame.setContentPane(new ganarNivel(4)); // Pasar a nivel 4
                            frame.setVisible(true);
                            dispose();
                            return;
                        } else {
                            JFrame frame = new JFrame();
                            frame.setSize(1000, 700);
                            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            frame.setContentPane(new ganarNivel(3)); // Pasar a nivel 3
                            frame.setVisible(true);
                            dispose();
                            return;
                        }
                    }
                }
                contentPane.revalidate();
            }
            Rectangle jugadorWorld11 = getJugadorWorldRect();
            for (enemigo e1 : enemigos) {
                if (jugadorWorld11.intersects(e1.getWorldRect())) {
                    perder();
                    return;
                }
            }
        });

        gameTimer.start();

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                requestFocusInWindow();
                int groundY = contentPane.getHeight() - GROUND_H - player.HEIGHT;
                int panelW = contentPane.getWidth();
                int centerX = panelW / 2 - player.WIDTH / 2;

                jugador.setX(centerX);
                jugador.setY(groundY);
                mario.setBounds(jugador.getX(), jugador.getY(), player.WIDTH, player.HEIGHT);
                contentPane.repaint();
                spawnEnemigos();
                lastPlayerY = jugador.getY();
            }
        });
    }

    private boolean puedeMoverseA(int nuevaX) {
        int jugadorY = jugador.getY();
        Rectangle nuevoRect = new Rectangle(mapOffset + nuevaX, jugadorY, player.WIDTH, player.HEIGHT);
        for (Rectangle tuberia : tuberias) {
            if (nuevoRect.intersects(tuberia)) {
                int jugadorBottom = jugadorY + player.WIDTH;
                int tuberiaTop = tuberia.y;
                if (jugador.isEnAire() && jugadorBottom <= tuberiaTop) continue;
                int margenVertical = 5;
                if (jugadorBottom >= tuberiaTop - margenVertical && jugadorBottom <= tuberiaTop + margenVertical) continue;
                return false;
            }
        }
        return true;
    }

    private ImageIcon load(String path) {
        Image img = new ImageIcon(path).getImage().getScaledInstance(player.WIDTH, player.HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void drawBackground(Graphics2D g2, Image img, int offset, double factor) {
        if (img == null)
            return;
        int iw = img.getWidth(null), ih = img.getHeight(null);
        if (iw <= 0 || ih <= 0)
            return;
        int panelW = contentPane.getWidth(), panelH = contentPane.getHeight();
        int drawH = panelH, drawW = (int) Math.round((double) iw * drawH / ih);
        int parallaxX = (int) Math.floor(-(offset * factor));
        int x0 = parallaxX % drawW;
        if (x0 > 0)
            x0 -= drawW;
        for (int x = x0; x < panelW; x += drawW) {
            g2.drawImage(img, x, 0, drawW, drawH, null);
        }
    }

    private void drawGround(Graphics2D g2, Image tile, int posY, int height, int offset) {
        if (tile == null)
            return;

        int tileW = tile.getWidth(null);
        if (tileW <= 0)
            return;

        if (!huecosGenerados) {
            generarHuecos(tileW);
            generarTuberias();
            huecosGenerados = true;
        }

        int panelW = contentPane.getWidth();
        int primerTile = offset / tileW;
        int tilesVisibles = (panelW / tileW) + 2;

        for (int i = primerTile; i < primerTile + tilesVisibles; i++) {
            int xTileInicio = i * tileW;
            int xTileFin = xTileInicio + tileW;
            int segmentoInicio = xTileInicio;

            for (Rectangle hueco : huecosRect) {
                if (hueco.x + hueco.width < xTileInicio || hueco.x > xTileFin) {
                    continue;
                }

                if (segmentoInicio < hueco.x && hueco.x > xTileInicio) {
                    int drawX = Math.max(segmentoInicio, xTileInicio) - offset;
                    int drawWidth = Math.min(hueco.x, xTileFin) - Math.max(segmentoInicio, xTileInicio);
                    int srcX = Math.max(segmentoInicio, xTileInicio) - xTileInicio;

                    if (drawWidth > 0) {
                        g2.drawImage(tile, drawX, posY, drawX + drawWidth, posY + height,
                                srcX, 0, srcX + drawWidth, height, null);
                    }
                }

                segmentoInicio = hueco.x + hueco.width;
            }

            if (segmentoInicio < xTileFin) {
                int drawX = segmentoInicio - offset;
                int drawWidth = xTileFin - segmentoInicio;
                int srcX = segmentoInicio - xTileInicio;

                if (drawWidth > 0 && srcX >= 0 && srcX < tileW) {
                    g2.drawImage(tile, drawX, posY, drawX + drawWidth, posY + height,
                            srcX, 0, Math.min(srcX + drawWidth, tileW), height, null);
                }
            }
        }
    }

    private void drawTuberias(Graphics2D g2, int offset) {
        if (imgTuberia == null) return;

        int panelW = contentPane.getWidth();
        int groundY = contentPane.getHeight() - GROUND_H;

        for (Rectangle tuberia : tuberias) {
            int tuberiaX = tuberia.x - offset;

            if (tuberiaX + tuberia.width > 0 && tuberiaX < panelW) {
                int tuberiaY = groundY - tuberia.height;

                g2.drawImage(imgTuberia, tuberiaX, tuberiaY, tuberia.width, tuberia.height, null);

                if (imgSuelo != null) {
                    int bury = Math.max(PIPE_EMBED_MIN, Math.min(PIPE_EMBED_MAX, tuberia.height / 10));
                    int overlayY = groundY - bury;
                    int tileW = imgSuelo.getWidth(null);

                    for (int x = tuberiaX; x < tuberiaX + tuberia.width; x += tileW) {
                        int drawW = Math.min(tileW, tuberiaX + tuberia.width - x);
                        g2.drawImage(
                            imgSuelo,
                            x, overlayY, x + drawW, groundY,
                            0, 0, drawW, bury,
                            null
                        );
                    }
                }
            }
        }
    }

    private void generarHuecos(int anchoTile) {
        huecosRect.clear();
        int anchoHueco = 150;
        int distanciaMinima = 500;
        int distanciaMaxima = 2000;
        int posicionActual = 2000;

        while (posicionActual < MAP_LIMIT - 1000) {
            huecosRect.add(new Rectangle(posicionActual, 0, anchoHueco, GROUND_H));
            int distancia = distanciaMinima + (int) (Math.random() * (distanciaMaxima - distanciaMinima));
            posicionActual += anchoHueco + distancia;
        }
        System.out.println("Huecos generados: " + huecosRect.size());
    }

    private void generarTuberias() {
        tuberias.clear();
        int anchoPipe = 120;
        int[] alturasDisponibles = { 100, 120, 140, 160, 200};
        int distanciaMinima = 200;
        int distanciaMaxima = 800;
        int posicionActual = 2500;
        int groundY = contentPane.getHeight() - GROUND_H;

        while (posicionActual < MAP_LIMIT - 1000) {
            boolean cercaDeHueco = false;
            for (Rectangle hueco : huecosRect) {
                if (Math.abs(posicionActual - hueco.x) < 400) {
                    cercaDeHueco = true;
                    break;
                }
            }
            if (!cercaDeHueco) {
                int altoPipe = alturasDisponibles[(int) (Math.random() * alturasDisponibles.length)];
                tuberias.add(new Rectangle(posicionActual, groundY - altoPipe, anchoPipe, altoPipe));
            }
            int distancia = distanciaMinima + (int) (Math.random() * (distanciaMaxima - distanciaMinima));
            posicionActual += distancia;
        }
        System.out.println("Tuberías generadas: " + tuberias.size());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) {
            leftPressed = true;
            facingLeft = true;
            if (!jugador.isEnAire())
                mario.setIcon(walkLeft);
        }
        if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) {
            rightPressed = true;
            facingLeft = false;
            if (!jugador.isEnAire())
                mario.setIcon(walkRight);
        }
        if (k == KeyEvent.VK_SPACE && !jugador.isEnAire()) {
            jugador.jump();
            mario.setIcon(jumpIcon);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A)
            leftPressed = false;
        if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D)
            rightPressed = false;

        if (!jugador.isEnAire()) {
            if (leftPressed)
                mario.setIcon(walkLeft);
            else if (rightPressed)
                mario.setIcon(walkRight);
            else
                mario.setIcon(facingLeft ? idleLeft : idleRight);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void perder() {
        gameTimer.stop();
        JFrame frame = new JFrame();
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new gameOver());
        frame.setVisible(true);
        dispose();
    }

    private void ganar() {
        gameTimer.stop();
        JFrame frame = new JFrame();
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new ganarNivel(nivel + 1));
        frame.setVisible(true);
        dispose();
    }
    
    private void spawnEnemigos() {
        int yBase = contentPane.getHeight() - GROUND_H - enemigo.HEIGHT;
        int[] xs = { 3200, 5200, 7800 };
        for (int x : xs) {
            enemigo e = new enemigo(x, yBase, enemy, enemy);
            enemigos.add(e);
            contentPane.add(e.getLabel());
        }
        if (nivel == 2) {
            jefeIcon = new ImageIcon("src/resources/enemigoLVL2.png");
            int jefeX = MAP_LIMIT - 300; // Más cerca del final visible
            int jefeY = contentPane.getHeight() - GROUND_H - enemigo.HEIGHT;
            jefeFinal = new enemigo(jefeX, jefeY, jefeIcon, jefeIcon, 3, true);
            enemigos.add(jefeFinal);
            contentPane.add(jefeFinal.getLabel());
        } else if (nivel == 3) {
            jefeIcon = new ImageIcon("src/resources/enemigoLVL3 (1).png");
            int jefeX = MAP_LIMIT - 300;
            int jefeY = contentPane.getHeight() - GROUND_H - enemigo.HEIGHT;
            jefeFinal = new enemigo(jefeX, jefeY, jefeIcon, jefeIcon, 5, true);
            enemigos.add(jefeFinal);
            contentPane.add(jefeFinal.getLabel());
        } else if (nivel == 4) {
            jefeIcon = new ImageIcon("src/resources/enemigoLVL4 (1).png");
            int jefeX = MAP_LIMIT - 300;
            int jefeY = contentPane.getHeight() - GROUND_H - enemigo.HEIGHT;
            jefeFinal = new enemigo(jefeX, jefeY, jefeIcon, jefeIcon, 8, true, true);
            enemigos.add(jefeFinal);
            contentPane.add(jefeFinal.getLabel());
        }
    }

    private ImageIcon loadEnemy(String path) {
        Image img = new ImageIcon(path).getImage().getScaledInstance(enemigo.WIDTH, enemigo.HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private Rectangle getJugadorWorldRect() {
        return new Rectangle(mapOffset + jugador.getX(), jugador.getY(), player.WIDTH, player.HEIGHT);
    }

    private boolean esParedContraTuberia(Rectangle futuroJugadorWorld, boolean enAire) {
        int jugadorBottom = futuroJugadorWorld.y + futuroJugadorWorld.height;
        for (Rectangle t : tuberias) {
            if (!futuroJugadorWorld.intersects(t)) continue;
            int tuberiaTop = t.y;
            if (enAire && jugadorBottom <= tuberiaTop + PIPE_PASS_TOL) {
                continue;
            }
            if (Math.abs(jugadorBottom - tuberiaTop) <= PIPE_PASS_TOL) {
                continue;
            }
            return true;
        }
        return false;
    }

    private String getFondoPorNivel() {
        if (nivel == 2) {
            return "src/resources/fondoLVL2.png";
        } else if (nivel == 3) {
            return "src/resources/fondoLVL3.png";
        } else if (nivel == 4) {
            return "src/resources/fondoLVL4.png";
        }
        return "src/resources/fondo.png";
    }

    private void drawBanderaFinal(Graphics2D g2, int offset) {
        if (imgBanderaFinal == null || rectBanderaFinal == null) return;
        int x = rectBanderaFinal.x - offset;
        int y = rectBanderaFinal.y;
        g2.drawImage(imgBanderaFinal, x, y, rectBanderaFinal.width, rectBanderaFinal.height, null);
    }
}
