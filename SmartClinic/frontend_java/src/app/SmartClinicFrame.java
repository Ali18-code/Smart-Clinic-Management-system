package app;

import app.panels.EmergencyPanel;
import app.panels.PatientPanel;
import java.awt.*;
import javax.swing.*;

public class SmartClinicFrame extends JFrame {

    public SmartClinicFrame() {
        setTitle("Smart Clinic Management System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(41, 128, 185));  // Rich blue color
        header.setPreferredSize(new Dimension(100, 70));
        header.setLayout(new BorderLayout());

        // Title label
        JLabel title = new JLabel("Smart Clinic Management System", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Tabs (JTabbedPane)
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Set background for individual tabs
        tabs.setBackgroundAt(0, new Color(52, 152, 219));  // Set blue for Patients tab
        tabs.setBackgroundAt(1, new Color(52, 152, 219));  // Set blue for Emergency tab

        // Set foreground for individual tabs (text color)
        tabs.setForegroundAt(0, Color.WHITE);
        tabs.setForegroundAt(1, Color.WHITE);

        // Add tabs with custom panels
        tabs.addTab("Patients", new PatientPanel());
        tabs.addTab("Emergency", new EmergencyPanel());

        add(tabs, BorderLayout.CENTER);

        // Add panel border and shadows for depth
        tabs.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tabs.setOpaque(true);

        // Main frame background color
        getContentPane().setBackground(Color.WHITE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SmartClinicFrame().setVisible(true));
    }
}
