package app;

import java.awt.*;
import javax.swing.*;

public class SplashScreen extends JFrame {

    public SplashScreen() {
        // Create a label with a message or logo
        JLabel label = new JLabel("Welcome to Smart Clinic!", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 30));
        label.setForeground(Color.WHITE);

        // Set background color
        getContentPane().setBackground(new Color(52, 152, 219));

        // Add the label to the frame
        add(label);

        // Set frame properties
        setSize(500, 300);
        setLocationRelativeTo(null);
        setUndecorated(true); // Hide title bar
        setVisible(true);

        // Timer to close the splash screen after 3 seconds and show the main window
        new Timer(3000, e -> {
            dispose(); // Close splash screen
            SwingUtilities.invokeLater(() -> new Main());  // Open the main window
        }).start();
    }

    public static void main(String[] args) {
        new SplashScreen();  // Show splash screen on startup
    }
}
