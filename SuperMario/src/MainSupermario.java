import GUI.ventanaPrincipal;
import java.awt.EventQueue;

public class MainSupermario {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ventanaPrincipal ventana = new ventanaPrincipal();
                ventana.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}