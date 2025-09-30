package GUI;

import javax.swing.*;
import java.awt.*;

public class gameOver extends JPanel {
    public gameOver() {
        setBackground(Color.BLACK);
        setLayout(null);

        JLabel title = new JLabel("GAME OVER", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 64));
        title.setForeground(Color.RED);
        title.setBounds(300, 100, 400, 100);
        add(title);

        JButton btnRetry = new JButton("Reintentar");
        btnRetry.setBounds(400, 250, 200, 40);
        btnRetry.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
            new ventanaPrincipal(1).setVisible(true); // reinicia el juego en nivel 1
        });
        add(btnRetry);
    }
}