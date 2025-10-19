package GUI;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class jugador {

    public enum Direccion { UP, DOWN, LEFT, RIGHT }

    private int spriteSize = 64; 
    private int x, y;
    private int ancho, alto;
    private int velocidad = 5;
    private Direccion dir = Direccion.DOWN;

    private Image sprUp, sprDown, sprLeft, sprRight;

    public jugador(int startX, int startY) {
        this.x = startX;
        this.y = startY;

        sprDown  = new ImageIcon("src/resources/images/bombritoAbajo.png").getImage();
        sprUp    = new ImageIcon("src/resources/images/bombritoArriba.png").getImage();
        sprLeft  = new ImageIcon("src/resources/images/bombritoIzquierda.png").getImage();
        sprRight = new ImageIcon("src/resources/images/bombritoDerecha.png").getImage();

        this.ancho = spriteSize;
        this.alto = spriteSize;
        
    }

    public void moveUp()    { y -= velocidad; dir = Direccion.UP; }
    public void moveDown()  { y += velocidad; dir = Direccion.DOWN; }
    public void moveLeft()  { x -= velocidad; dir = Direccion.LEFT; }
    public void moveRight() { x += velocidad; dir = Direccion.RIGHT; }


    public void clampTo(Rectangle worldBounds) {
        if (worldBounds == null) return;
        
        if (x < worldBounds.x) x = worldBounds.x;
        if (y < worldBounds.y) y = worldBounds.y;
        
        if (x + ancho > worldBounds.x + worldBounds.width) 
            x = worldBounds.x + worldBounds.width - ancho;
        if (y + alto > worldBounds.y + worldBounds.height) 
            y = worldBounds.y + worldBounds.height - alto;
    }

    public void update(Rectangle worldBounds) {
        clampTo(worldBounds);
    }

    public void draw(Graphics2D g) {
        Image img = switch (dir) {
            case UP    -> sprUp;
            case DOWN  -> sprDown;
            case LEFT  -> sprLeft;
            case RIGHT -> sprRight;
        };
        g.drawImage(img, x, y, spriteSize, spriteSize, null);
    }

    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public void setVelocidad(int v) { this.velocidad = Math.max(1, v); }
    public int getX() { return x; }
    public int getY() { return y; }
    public Rectangle getBounds() { return new Rectangle(x, y, spriteSize, spriteSize); }
}

