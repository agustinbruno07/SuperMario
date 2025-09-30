package GUI;
import javax.swing.*;
import java.awt.*;

public class ganarNivel extends JPanel {
    public ganarNivel() {
        setLayout(null);
        setBounds(0, 0, 1000, 700);
        setOpaque(false);
        setBackground(Color.BLACK);
        JLabel titulo = new JLabel("¡Ganaste!");
        titulo.setFont(new Font("Tahoma", Font.ITALIC, 38));
        titulo.setBounds(408, 84, 192, 91);
        add(titulo);

        JButton btnNivelSiguiente = new JButton("Pasar al siguiente nivel");
        btnNivelSiguiente.setBounds(400, 200, 200, 40);
        btnNivelSiguiente.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
            new ventanaPrincipal().setVisible(true); // o la lógica para el siguiente nivel
        });
        btnNivelSiguiente.setBackground(new Color(34, 139, 34));
        add(btnNivelSiguiente);
    }
}
