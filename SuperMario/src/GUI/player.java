package GUI;



public class player {
    public static final int WIDTH = 80;   // ancho de la paleta
    public static final int HEIGHT = 80;  // alto de la paleta
    private int x;  
    private int y;  
    private final int speed = 8; // Velocidad de movimiento
    private final int WINDOW_WIDTH = 1000;
   
    
    public player(int x, int y) { 
        this.x = x;
        this.y = y;
    }
    

    public void moveLeft() { 
        if (x - speed >= 0) {// Se fija que no se vaya para la izquierda el jugador
            x -= speed;
        }
    }

    public void moveRight() { 
        if (x + speed + WIDTH <= WINDOW_WIDTH) {// Se fija que no se vaya para la derecha el jugador
            x += speed;
        }
    }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return WIDTH; }
    public int getHeight() { return HEIGHT; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}   