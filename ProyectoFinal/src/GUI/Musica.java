
package GUI;
import javax.sound.sampled.*;

import java.io.File;

public class Musica {
    private static Clip clipActual;

    public static void reproducir(String ruta) {
        detener();
        try {
            File archivo = new File(ruta);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivo);
            clipActual = AudioSystem.getClip();
            clipActual.open(audioStream);
            clipActual.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void reproducirLoop(String ruta) {
        detener();
        try {
            File archivo = new File(ruta);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivo);
            clipActual = AudioSystem.getClip();
            clipActual.open(audioStream);
            clipActual.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Detiene la música/sonido actual
    public static void detener() {
        if (clipActual != null && clipActual.isRunning()) {
            clipActual.stop();
            clipActual.close();
        }
    }
}
