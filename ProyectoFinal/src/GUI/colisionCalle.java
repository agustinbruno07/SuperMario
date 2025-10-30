package GUI;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class colisionCalle {
    
    private BufferedImage mascaraColision;
    private boolean obstacleIsBright = true; // true: bright pixels are obstacles; false: dark pixels are obstacles
    private static final int BRIGHTNESS_THRESHOLD = 128;
    
    public colisionCalle(String rutaMascara) {
        try {
            File archivo = new File(rutaMascara);
            mascaraColision = ImageIO.read(archivo);
            
            if (mascaraColision != null) {
                System.out.println("✅ Máscara de colisión cargada: " + mascaraColision.getWidth() + "x" + mascaraColision.getHeight());
                // Detectar automáticamente si la máscara marca obstáculos con píxeles claros o oscuros.
                // Muestreamos la imagen con un paso para no iterar todos los píxeles si es muy grande.
                int w = mascaraColision.getWidth();
                int h = mascaraColision.getHeight();
                long brightCount = 0;
                long total = 0;
                int maxSamples = 200_000; // límite de muestras para rendimiento
                int step = 1;
                long approx = (long) w * h;
                if (approx > maxSamples) {
                    // calcular paso para muestrear aproximadamente maxSamples píxeles
                    step = (int) Math.max(1, Math.sqrt((double) (w * h) / maxSamples));
                }
                for (int y = 0; y < h; y += step) {
                    for (int x = 0; x < w; x += step) {
                        int argb = mascaraColision.getRGB(x, y);
                        int alpha = (argb >> 24) & 0xFF;
                        int red   = (argb >> 16) & 0xFF;
                        int green = (argb >> 8)  & 0xFF;
                        int blue  = argb & 0xFF;
                        int brillo = (red + green + blue) / 3;
                        // Considerar píxeles transparentes como no brillantes para el muestreo
                        if (alpha == 0) {
                            // ignorar
                        } else {
                            if (brillo > BRIGHTNESS_THRESHOLD) brightCount++;
                            total++;
                        }
                    }
                }
                if (total > 0) {
                    double pctBright = (double) brightCount / total;
                    // Si la mayoría de píxeles muestreados son brillantes, asumimos que los obstáculos son píxeles oscuros
                    // (porque fondo brillante ocupa más área). Si la mayoría son oscuros, invertimos.
                    // Para mayor robustez, interpretamos que si pctBright < 0.5 entonces obstacles are bright.
                    // Simplificamos: obstacleIsBright = pctBright >= 0.5 ? false : true;
                    obstacleIsBright = pctBright < 0.5; // true -> obstacles are bright, false -> obstacles are dark
                    System.out.println("[colisionCalle] muestreo brillo: brightCount=" + brightCount + ", total=" + total + ", pctBright=" + pctBright + ", obstacleIsBright=" + obstacleIsBright);
                } else {
                    // fallback: suponer que píxeles brillantes representan obstáculos (comportamiento original)
                    obstacleIsBright = true;
                    System.out.println("[colisionCalle] muestreo inválido, usando fallback obstacleIsBright=true");
                }
            } else {
                System.err.println("❌ mascaraColision es NULL");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al cargar máscara de colisión: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public boolean hayColision(Rectangle jugadorBounds, int anchoVentana, int altoVentana) {
        if (mascaraColision == null) {
            return false;
        }

        // Calcular la escala
        double escalaX = (double) mascaraColision.getWidth() / anchoVentana;
        double escalaY = (double) mascaraColision.getHeight() / altoVentana;

        // Convertir coordenadas del jugador al sistema de la máscara
        int xMascara = (int)(jugadorBounds.x * escalaX);
        int yMascara = (int)(jugadorBounds.y * escalaY);
        int anchoMascara = Math.max(1, (int)(jugadorBounds.width * escalaX));
        int altoMascara = Math.max(1, (int)(jugadorBounds.height * escalaY));

        // Puntos a verificar (centro + esquinas con margen)
        int[][] puntosAVerificar = {
            { xMascara + anchoMascara/2, yMascara + altoMascara/2 }, // Centro
            { xMascara + 5, yMascara + 5 },                          // Sup izq
            { xMascara + anchoMascara - 5, yMascara + 5 },           // Sup der
            { xMascara + 5, yMascara + altoMascara - 5 },            // Inf izq
            { xMascara + anchoMascara - 5, yMascara + altoMascara - 5 } // Inf der
        };

        for (int[] punto : puntosAVerificar) {
            int px = punto[0];
            int py = punto[1];

            // Comprobar límites
            if (px >= 0 && px < mascaraColision.getWidth() &&
                py >= 0 && py < mascaraColision.getHeight()) {

                int argb = mascaraColision.getRGB(px, py);

                // Extraer alpha + componentes RGB
                int alpha = (argb >> 24) & 0xFF;
                int red   = (argb >> 16) & 0xFF;
                int green = (argb >> 8)  & 0xFF;
                int blue  = argb & 0xFF;

                // Si la máscara usa transparencia para bloquear: alpha == 0 => BLOQUEADO
                if (alpha == 0) {
                    return true;
                }

                int brillo = (red + green + blue) / 3;

                // Determinar bloqueo según la regla detectada al cargar la máscara
                if (obstacleIsBright) {
                    // Obstáculos representados por píxeles brillantes
                    if (brillo > BRIGHTNESS_THRESHOLD) return true;
                } else {
                    // Obstáculos representados por píxeles oscuros
                    if (brillo < BRIGHTNESS_THRESHOLD) return true;
                }
            } else {
                // Si el punto está fuera de la máscara, podemos considerar bloqueo opcionalmente.
                // Por ahora lo ignoramos (no colisión) — si prefieres tratar fuera como bloqueado,
                // devuelve true aquí.
            } 	
        }

        return false; // No hay colisión detectada
    }

}