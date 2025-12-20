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
            new SplashScreen(3000, Main::showLoginScreen);
        });
    }

    // ================= LOGIN SCREEN =================
    public static void showLoginScreen() {
        JFrame loginFrame = new JFrame("System Login");
        loginFrame.setSize(400, 320);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setResizable(false);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(44, 62, 80));

        JLabel title = new JLabel("ADMIN LOGIN");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBounds(115, 30, 200, 30);
        panel.add(title);

        JTextField userField = new JTextField("admin");
        userField.setBounds(75, 90, 250, 40);
        userField.setBorder(BorderFactory.createTitledBorder("Username"));
        panel.add(userField);

        JPasswordField passField = new JPasswordField("1234");
        passField.setBounds(75, 150, 250, 40);
        passField.setBorder(BorderFactory.createTitledBorder("Password"));
        panel.add(passField);

        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBounds(75, 210, 250, 40);
        loginBtn.setBackground(new Color(46, 204, 113));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(e -> {
            loginFrame.dispose();
            openMainWindow();
        });
        panel.add(loginBtn);

        loginFrame.add(panel);
        loginFrame.setVisible(true);
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
