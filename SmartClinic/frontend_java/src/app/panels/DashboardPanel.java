package app.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        
        // Create Patients button with hover effect
        JButton btnPatients = createRoundedButton("Patients");
        btnPatients.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Switch to Patient Panel
                ((CardLayout) getParent().getLayout()).show(getParent(), "PatientPanel");
            }
        });

        // Create Emergency button with hover effect
        JButton btnEmergency = createRoundedButton("Emergency");
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

    // Method to create rounded buttons with hover effects
    private JButton createRoundedButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(34, 193, 195)); // Aqua color for the button
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBorder(BorderFactory.createLineBorder(new Color(34, 193, 195), 2, true));
        button.setBorderPainted(true);

        // Adding hover effect to buttons
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker()); // Darken on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(34, 193, 195)); // Reset to original color
            }
        });

        // Set button size
        button.setPreferredSize(new Dimension(200, 50));
        return button;
    }
}
