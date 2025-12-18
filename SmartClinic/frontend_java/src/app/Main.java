package app;

import app.panels.DashboardPanel;
import app.panels.EmergencyPanel;
import app.panels.PatientPanel;
import java.awt.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set the Look and Feel to a modern style
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show the splash screen directly by creating an instance of SplashScreen
        SwingUtilities.invokeLater(() -> {
            new SplashScreen();  // Create instance of SplashScreen to show it
        });

        // Create the frame for the application
        JFrame frame = new JFrame("Smart Clinic Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null); // Center the window on the screen
        frame.setResizable(true); // Allow resizing the window

        // Create a CardLayout for switching between panels
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        // Create the panels
        DashboardPanel dashboardPanel = new DashboardPanel();
        PatientPanel patientPanel = new PatientPanel();
        EmergencyPanel emergencyPanel = new EmergencyPanel();

        // Add panels to the main panel with unique names
        mainPanel.add(dashboardPanel, "DashboardPanel");
        mainPanel.add(patientPanel, "PatientPanel");
        mainPanel.add(emergencyPanel, "EmergencyPanel");

        // Display the dashboard first
        cardLayout.show(mainPanel, "DashboardPanel");

        // Add the mainPanel to the frame
        frame.add(mainPanel);
        frame.setVisible(true);
    }
}
