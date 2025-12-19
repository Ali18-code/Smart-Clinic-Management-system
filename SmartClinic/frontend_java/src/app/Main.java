package app;

import app.panels.DashboardPanel;
import app.panels.EmergencyPanel;
import app.panels.PatientPanel;
import java.awt.*;
import javax.swing.*;

public class Main {

    // Entry point
    public static void main(String[] args) {

        // Set system look & feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show splash, then open main UI
        SwingUtilities.invokeLater(() -> {
            new SplashScreen(3000, Main::openMainWindow);
        });
    }

    // ================= MAIN APPLICATION WINDOW =================
    public static void openMainWindow() {

        JFrame frame = new JFrame("Smart Clinic Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 700);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

        // CardLayout container
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        // Panels
        DashboardPanel dashboardPanel = new DashboardPanel();
        PatientPanel patientPanel = new PatientPanel();
        EmergencyPanel emergencyPanel = new EmergencyPanel();

        // Add panels
        mainPanel.add(dashboardPanel, "DashboardPanel");
        mainPanel.add(patientPanel, "PatientPanel");
        mainPanel.add(emergencyPanel, "EmergencyPanel");

        // Show dashboard
        cardLayout.show(mainPanel, "DashboardPanel");

        frame.add(mainPanel);
        frame.setVisible(true);
    }
}
