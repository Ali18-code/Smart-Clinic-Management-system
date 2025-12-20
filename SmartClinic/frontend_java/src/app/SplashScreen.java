package app;

import java.awt.*;
import javax.swing.*;

public class SplashScreen extends JWindow {

    public SplashScreen(int duration, Runnable onFinish) {

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(30, 30, 30));
        content.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        JLabel title = new JLabel("SMART CLINIC", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Loading system modules...", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(Color.LIGHT_GRAY);

        content.add(title, BorderLayout.CENTER);
        content.add(sub, BorderLayout.SOUTH);

        setContentPane(content);
        setSize(500, 280);
        setLocationRelativeTo(null);

        setAlwaysOnTop(true);   // 🔴 CRITICAL
        setVisible(true);

        Timer timer = new Timer(duration, e -> {
            dispose();

            // Ensure main window opens after splash fully closes
            SwingUtilities.invokeLater(onFinish);
        });
        timer.setRepeats(false);
        timer.start();
    }
}

