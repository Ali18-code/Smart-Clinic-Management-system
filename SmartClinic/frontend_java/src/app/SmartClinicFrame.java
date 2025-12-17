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
        header.setBackground(new Color(52, 152, 219));
        header.setPreferredSize(new Dimension(100, 55));

        JLabel title = new JLabel("Smart Clinic Management System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(title);

        add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabs.addTab("Patients", new PatientPanel());
        tabs.addTab("Emergency", new EmergencyPanel());

        add(tabs, BorderLayout.CENTER);
    }
}


