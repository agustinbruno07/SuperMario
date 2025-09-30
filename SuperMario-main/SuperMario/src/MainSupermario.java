import GUI.ventanaInicio;
import java.awt.EventQueue;

public class MainSupermario{
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ventanaInicio ventana = new ventanaInicio();
                ventana.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}