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
	private final int MAP_LIMIT = 15000;
	private JLabel numNivel;
	private JLabel Vida;
	private JLabel numVida;
	private ArrayList<Integer> huecos = new ArrayList<>();
	private ArrayList<Rectangle> huecosRect = new ArrayList<>();
	private ArrayList<Rectangle> tuberias = new ArrayList<>();
	private boolean huecosGenerados = false;
	private final int PIPE_EMBED_MIN = 6;   // px mínimos a tapar de las tuberias
	private final int PIPE_EMBED_MAX = 18;  // px máximos a tapar de las tuberias
	private static final int PIPE_PASS_TOL = 6; // px de tolerancia de tuberias para que no se teletransporte
	private ArrayList<enemigo> enemigos = new ArrayList<>();
	private ImageIcon enemy;
	private int lastPlayerY = 0;
	private boolean cayendoEnHueco = false;
	
	


	public ventanaPrincipal() {
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
				g2.dispose();
			}
		};
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		imgFondo = new ImageIcon("src/resources/fondo.png").getImage();
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

		ventanaInicio.iniciarMusicaFondo("src/resources/sound/musicaJuego.wav");

		gameTimer = new Timer(16, e -> {
			int panelW = contentPane.getWidth();
			if (panelW <= 0)
				panelW = getWidth();

			int centerX = panelW / 2 - player.WIDTH / 2;

			int step = 10;

			// Movimiento IZQUIERDA con bloqueo por tubería
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
					    // clamp: dejar al jugador pegado a la derecha de la tubería
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

			// Movimiento DERECHA con bloqueo por tubería
			if (rightPressed) {
				if (jugador.getX() < centerX) {
					int nuevaX = jugador.getX() + step;
					if (puedeMoverseA(nuevaX)) {
						jugador.setX(nuevaX);
					}
				} else {
					int nuevoOffset = Math.min(MAP_LIMIT - panelW, mapOffset + step);
					Rectangle futuro = new Rectangle(nuevoOffset + jugador.getX(), jugador.getY(), player.WIDTH, player.HEIGHT);
					if (esParedContraTuberia(futuro, jugador.isEnAire())) {
					    Rectangle pared = null;
					    for (Rectangle t : tuberias) if (futuro.intersects(t)) { pared = t; break; }
					    if (pared != null) {
					        nuevoOffset = pared.x - player.WIDTH - jugador.getX();
					    }
					    nuevoOffset = Math.max(0, Math.min(nuevoOffset, MAP_LIMIT - panelW));
					}
					mapOffset = nuevoOffset;
			    }
			}

			int groundY = contentPane.getHeight() - GROUND_H - player.HEIGHT;

			// Verificar si el jugador está sobre un hueco
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

			// Verificar si está sobre una tubería (pies cerca del tope)
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
				ganar();
				return;
			}
			int sueloYEnem = contentPane.getHeight() - GROUND_H - enemigo.HEIGHT;

			for (enemigo e1 : enemigos) {
			    boolean eSobreHueco = false;
			    int eIzq = e1.getWorldX();
			    int eDer = eIzq + enemigo.WIDTH;
			    int eCentro = eIzq + enemigo.WIDTH / 2;

			    for (Rectangle hueco : huecosRect) {
			        if (eDer > hueco.x && eIzq < hueco.x + hueco.width) { eSobreHueco = true; break; }
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

			    int groundYEnemigo = eSobreHueco ? contentPane.getHeight() + 500
			                        : (tuberiaDebajoE != null ? mejorY : sueloYEnem);

			    e1.updateHorizontal(tuberias, MAP_LIMIT);
			    e1.updateVertical(groundYEnemigo);
			    e1.syncSprite(mapOffset);
			}
			
			Rectangle jugadorWorld = new Rectangle(mapOffset + jugador.getX(), jugador.getY(), player.WIDTH, player.HEIGHT);
			java.util.List<enemigo> paraEliminar = new ArrayList<>();

			for (enemigo e1 : enemigos) {
			    Rectangle enemyWorld = e1.getWorldRect();
			    if (jugadorWorld.intersects(enemyWorld)) {

			        int enemyTop     = e1.getY();
			        int playerTop    = jugador.getY();
			        int playerBottom = jugador.getY() + player.HEIGHT;
			        int prevBottom   = lastPlayerY + player.HEIGHT;

			       
			        int TOL = 6; // tolerancia en px
			        boolean desdeArriba = prevBottom <= enemyTop + TOL && playerBottom >= enemyTop;

			        if (desdeArriba) {
			           
			            paraEliminar.add(e1);

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

			// remover enemigos muertos fuera del loop
			if (!paraEliminar.isEmpty()) {
			    for (enemigo e1 : paraEliminar) {
			        contentPane.remove(e1.getLabel());
			        enemigos.remove(e1);
			    }
			    contentPane.revalidate();
			}
			Rectangle jugadorWorld11 = getJugadorWorldRect();
			for (enemigo e1 : enemigos) {
			    if (jugadorWorld11.intersects(e1.getWorldRect())) {
			        perder();  // detiene el timer y cambia de pantalla
			        return;    // salimos del tick para evitar dobles llamadas
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

	// Función que bloquea el movimiento lateral si hay tubería y el jugador no está saltando por encima
	private boolean puedeMoverseA(int nuevaX) {
		int jugadorY = jugador.getY();
		Rectangle nuevoRect = new Rectangle(mapOffset + nuevaX, jugadorY, player.WIDTH, player.HEIGHT);
		for (Rectangle tuberia : tuberias) {
			if (nuevoRect.intersects(tuberia)) {// hay colisión
				int jugadorBottom = jugadorY + player.HEIGHT;// pies del jugador
				int tuberiaTop = tuberia.y;// tope de la tubería

				// Permitir pasar si está saltando y sus pies están por encima de la tubería
				if (jugador.isEnAire() && jugadorBottom <= tuberiaTop) {
					continue;
				}

				// Permitir estar sobre la tubería (pies cerca del tope)
				int margenVertical = 5;
				if (jugadorBottom >= tuberiaTop - margenVertical && jugadorBottom <= tuberiaTop + margenVertical) {
					continue;
				}

				// Bloquear movimiento lateral
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

	            // 1) Dibujar la tubería normal (sin moverla)
	            g2.drawImage(imgTuberia, tuberiaX, tuberiaY, tuberia.width, tuberia.height, null);

	            // 2) “Enterrar” visualmente la base con una franja del suelo por encima
	            if (imgSuelo != null) {
	                int bury = Math.max(PIPE_EMBED_MIN, Math.min(PIPE_EMBED_MAX, tuberia.height / 10));
	                int overlayY = groundY - bury;
	                int tileW = imgSuelo.getWidth(null);

	                for (int x = tuberiaX; x < tuberiaX + tuberia.width; x += tileW) {
	                    int drawW = Math.min(tileW, tuberiaX + tuberia.width - x);
	                    // Tomo la franja superior del sprite de suelo (0..bury) y la dibujo sobre la base
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

		int anchoPipe = 120; // Tuberías más anchas
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
		// No se usa
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
		frame.setContentPane(new ganarNivel());
		frame.setVisible(true);
		dispose();
	}
	private void spawnEnemigos() {
		
	    int yBase = contentPane.getHeight() - GROUND_H - enemigo.HEIGHT;
	    
	    int[] xs = { 3200, 5200, 7800 };
	    for (int x : xs) {
	        enemigo e = new enemigo(x, yBase, enemy, enemy);
	        enemigos.add(e);
	        contentPane.add(e.getLabel()); // agregamos su JLabel al panel
	    }
	}
	private ImageIcon loadEnemy(String path) {
	    Image img = new ImageIcon(path).getImage()
	        .getScaledInstance(enemigo.WIDTH, enemigo.HEIGHT, Image.SCALE_SMOOTH);
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
        // Si está en el aire y sus pies quedan por encima del tope (+ tol), NO es pared
        if (enAire && jugadorBottom <= tuberiaTop + PIPE_PASS_TOL) {
            continue;
        }
        // Caso especial: apoyado “justo” sobre el tope, tampoco bloquear lateral
        if (Math.abs(jugadorBottom - tuberiaTop) <= PIPE_PASS_TOL) {
            continue;
        }
        return true; // esto sí es pared
    }
    return false;
}
}
