package app.panels;

import app.util.BackendRunner;
import app.util.FileUtil;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class DashboardPanel extends JPanel {

    // These must be class-level variables to persist across refreshes
    private JLabel totalPatientsValue, totalEmergenciesValue, criticalEmergenciesValue, normalEmergenciesValue;
    private boolean isRefreshing = false; // Flag to prevent overlapping threads

    public DashboardPanel() {
        setLayout(new BorderLayout(30, 30));
        setBackground(new Color(44, 62, 80)); // Professional Navy
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JLabel title = new JLabel("Smart Clinic Management System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Center Container
        JPanel centerContainer = new JPanel(new BorderLayout(30, 30));
        centerContainer.setOpaque(false);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 20));
        statsPanel.setOpaque(false);

        // Initialize persistent labels
        totalPatientsValue = createValueLabel();
        totalEmergenciesValue = createValueLabel();
        criticalEmergenciesValue = createValueLabel();
        normalEmergenciesValue = createValueLabel();

        statsPanel.add(modernStatCard("Total Patients", totalPatientsValue, new Color(46, 204, 113)));
        statsPanel.add(modernStatCard("Total Emergencies", totalEmergenciesValue, new Color(231, 76, 60)));
        statsPanel.add(modernStatCard("Critical Cases", criticalEmergenciesValue, new Color(192, 57, 43)));
        statsPanel.add(modernStatCard("Normal Cases", normalEmergenciesValue, new Color(52, 152, 219)));

        centerContainer.add(statsPanel, BorderLayout.NORTH);

        // Action Buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 40));
        actions.setOpaque(false);

        JButton btnPatients = modernActionButton("Manage Patients", new Color(52, 73, 94));
        JButton btnEmergency = modernActionButton("Emergency Room", new Color(52, 73, 94));
        JButton btnRefresh = modernActionButton("Refresh Live Stats", new Color(26, 188, 156));

        btnPatients.addActionListener(e -> ((CardLayout) getParent().getLayout()).show(getParent(), "PatientPanel"));
        btnEmergency.addActionListener(e -> ((CardLayout) getParent().getLayout()).show(getParent(), "EmergencyPanel"));
        btnRefresh.addActionListener(e -> loadStatsFromBackend());

        actions.add(btnPatients);
        actions.add(btnEmergency);
        actions.add(btnRefresh);

        centerContainer.add(actions, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);

        // Automatic refresh every 10 seconds
        loadStatsFromBackend();
        new Timer(10000, e -> loadStatsFromBackend()).start(); 
    }

    private JLabel createValueLabel() {
        JLabel lbl = new JLabel("0", SwingConstants.CENTER); // Initialized with 0
        lbl.setFont(new Font("Segoe UI Semibold", Font.BOLD, 42));
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(false);
        return lbl;
    }

    private JPanel modernStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(new Color(255, 255, 255, 20)); // Adjusted transparency
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(accentColor, 2, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel t = new JLabel(title.toUpperCase(), SwingConstants.CENTER);
        t.setForeground(new Color(200, 214, 229));
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));

        card.add(t, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JButton modernActionButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(240, 60));
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }

    private void loadStatsFromBackend() {
        if (isRefreshing) return; // Prevent multiple simultaneous refreshes
        isRefreshing = true;

        new Thread(() -> {
            try {
                // Trigger backend to ensure latest data is generated
                FileUtil.writeText(BackendRunner.DATA_DIR + "patient_input.json", "{\"action\":\"VIEW_ALL\"}");
                BackendRunner.run("patient");

                String pData = safeRead(BackendRunner.DATA_DIR + "patients_db.json", BackendRunner.DATA_DIR + "patient_output.json");
                String eData = safeRead(BackendRunner.DATA_DIR + "emergency_db.json", BackendRunner.DATA_DIR + "emergency_output.json");

                int totalP = countOccurrences(pData, "\"id\"");
                int totalE = countOccurrences(eData, "\"name\"");
                int crit = countSeverityAtLeast(eData, 7);
                int norm = Math.max(0, totalE - crit);

                // Update UI safely on Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    totalPatientsValue.setText(String.valueOf(totalP));
                    totalEmergenciesValue.setText(String.valueOf(totalE));
                    criticalEmergenciesValue.setText(String.valueOf(crit));
                    normalEmergenciesValue.setText(String.valueOf(norm));
                    
                    // Repaint to ensure old text is cleared from the graphics buffer
                    repaint(); 
                });

            } catch (Exception ex) {
                System.err.println("Refresh Error: " + ex.getMessage());
            } finally {
                isRefreshing = false;
            }
        }).start();
    }

    private String safeRead(String path1, String path2) {
        try { return FileUtil.readText(path1); } 
        catch (Exception e1) {
            try { return FileUtil.readText(path2); } 
            catch (Exception e2) { return ""; }
        }
    }

    private int countOccurrences(String text, String token) {
        if (text == null || text.length() < 5) return 0;
        int count = 0, idx = 0;
        while ((idx = text.indexOf(token, idx)) != -1) {
            count++;
            idx += token.length();
        }
        return count;
    }

    private int countSeverityAtLeast(String json, int threshold) {
        if (json == null || json.isEmpty()) return 0;
        int count = 0, idx = 0;
        while ((idx = json.indexOf("\"severity\"", idx)) != -1) {
            int colon = json.indexOf(":", idx);
            if (colon != -1) {
                int j = colon + 1;
                while (j < json.length() && !Character.isDigit(json.charAt(j))) j++;
                StringBuilder num = new StringBuilder();
                while (j < json.length() && Character.isDigit(json.charAt(j))) num.append(json.charAt(j++));
                try {
                    if (!num.toString().isEmpty() && Integer.parseInt(num.toString()) >= threshold) count++;
                } catch (Exception ignore) {}
                idx = j;
            } else idx++;
        }
        return count;
    }
}