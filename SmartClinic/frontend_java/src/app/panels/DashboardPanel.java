package app.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DashboardPanel extends JPanel {
    public DashboardPanel() {
        setLayout(new BorderLayout());
        
        // Create a title label for the Dashboard
        JLabel title = new JLabel("Smart Clinic Management System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));  // 2 buttons, vertical layout
        buttonPanel.setBackground(new Color(41, 128, 185));  // Set a background color for the panel
        
        // Create Patients button
        JButton btnPatients = new JButton("Patients");
        btnPatients.setFont(new Font("Arial", Font.PLAIN, 18));
        btnPatients.setFocusPainted(false);
        btnPatients.setBackground(new Color(34, 193, 195));
        btnPatients.setForeground(Color.WHITE);
        btnPatients.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Switch to Patient Panel
                ((CardLayout) getParent().getLayout()).show(getParent(), "PatientPanel");
            }
        });

        // Create Emergency button
        JButton btnEmergency = new JButton("Emergency");
        btnEmergency.setFont(new Font("Arial", Font.PLAIN, 18));
        btnEmergency.setFocusPainted(false);
        btnEmergency.setBackground(new Color(34, 193, 195));
        btnEmergency.setForeground(Color.WHITE);
        btnEmergency.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Switch to Emergency Panel
                ((CardLayout) getParent().getLayout()).show(getParent(), "EmergencyPanel");
            }
        });

        // Add buttons to the buttonPanel
        buttonPanel.add(btnPatients);
        buttonPanel.add(btnEmergency);

        // Add the buttonPanel to the center of the Dashboard
        add(buttonPanel, BorderLayout.CENTER);

        // Set the background color of the Dashboard
        setBackground(new Color(52, 152, 219));  // Light blue background
    }
}
