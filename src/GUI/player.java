package GUI;

public class player {
    public static final int WIDTH = 60, HEIGHT = 60	;

    private int x, y;
    private final int speed = 20;
    private final int WINDOW_WIDTH = 1000;

    private int velY = 0;        
    private final int GRAV = 1;  
    private final int JUMP = -20;
    private boolean enAire = false;

    public player(int x, int y) { this.x = x; this.y = y; }

    public void moveLeft()  { 
    	if (x - speed >= 0) x -= speed;
    	}
    public void moveRight() {
        if (x + speed + WIDTH <= WINDOW_WIDTH) x += speed; 
        }

    public void jump() {
        if (!enAire) {      
            velY = JUMP;    
            enAire = true;
        }
    }

    // ahora el piso viene de la ventana
    public void update(int groundY) {
        if (enAire) {
            y += velY;
            velY += GRAV;
            if (y >= groundY) {
                y = groundY;
                velY = 0;
                enAire = false;
            }
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth()  { return WIDTH; }
    public int getHeight() { return HEIGHT; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public boolean isEnAire() { return enAire; }

    public void setEnAire(boolean enAire) {
        this.enAire = enAire;
    }
}
