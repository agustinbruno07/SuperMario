package GUI;
import javax.swing.*;
import java.awt.*;

public class ganarNivel extends JPanel {
    private int siguienteNivel = 2;

    public ganarNivel() {
        this(2); // Por defecto, pasa al nivel 2
    }

    public ganarNivel(int nivel) {
        setLayout(null);
        setBounds(0, 0, 1000, 700);
        setOpaque(false);
        setBackground(Color.BLACK);
        JLabel titulo = new JLabel("¡Ganaste!");
        titulo.setFont(new Font("Tahoma", Font.ITALIC, 38));
        titulo.setBounds(408, 84, 192, 91);
        add(titulo);
        siguienteNivel = nivel;
        JButton btnNivelSiguiente = new JButton("Pasar al siguiente nivel");
        btnNivelSiguiente.setBounds(400, 200, 200, 40);
        btnNivelSiguiente.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
            new ventanaPrincipal(siguienteNivel).setVisible(true);
        });
        btnNivelSiguiente.setBackground(new Color(34, 139, 34));
        add(btnNivelSiguiente);
    }
}